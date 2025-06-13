package ru.forum.whale.space.api.service;

import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.dto.PersonDto;
import ru.forum.whale.space.api.exception.*;
import ru.forum.whale.space.api.model.Person;
import ru.forum.whale.space.api.repository.PersonRepository;
import ru.forum.whale.space.api.security.PersonDetails;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final ModelMapper modelMapper;
    private final MinioClient minioClient;

    @Value("${minio.avatar-bucket}")
    private String avatarBucket;

    @PostConstruct
    private void initAvatarBucket() {
        try {
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(avatarBucket)
                            .build()
            );
            if (!found) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(avatarBucket)
                                .build()
                );
            }
        } catch (Exception e) {
            throw new GeneralMinioException("Ошибка при инициализации MinIO бакета: " + e.getMessage());
        }
    }

    public List<PersonDto> findAll(Sort sort) {
        return personRepository.findAll(sort).stream()
                .map(this::convertToPersonDto)
                .collect(Collectors.toList());
    }

    public PersonDto findByUsername(String username) {
        Optional<Person> person = personRepository.findByUsername(username);

        if (person.isEmpty()) {
            throw new ResourceNotFoundException("Пользователь не найден");
        }

        return convertToPersonDto(person.get());
    }

    public PersonDto findYourself() {
        PersonDetails personDetails = (PersonDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return convertToPersonDto(personDetails.getPerson());
    }

    private PersonDto convertToPersonDto(Person person) {
        return modelMapper.map(person, PersonDto.class);
    }

    public String generateAvatarUrl(String filename) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(avatarBucket)
                    .object(filename)
                    .build());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Аватар не найден");
        }
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(avatarBucket)
                            .object(filename)
                            .expiry(5, TimeUnit.MINUTES)
                            .build());
        } catch (Exception e) {
            throw new GeneralMinioException("Ошибка при генерации временной ссылки на аватар: " + e.getMessage());
        }
    }

    @Transactional
    public String uploadAvatar(MultipartFile file, HttpServletRequest request) {
        PersonDetails personDetails = ((PersonDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal());
        Person person = personDetails.getPerson();

        String contentType = file.getContentType();
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            throw new IllegalOperationException("Файл должен быть формата PNG или JPG/JPEG");
        }

        try {
            String avatarFileName = "avatar-" + person.getId();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(avatarBucket)
                            .object(avatarFileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(contentType)
                            .build()
            );

            person.setAvatarFileName(avatarFileName);
            personRepository.save(person);

            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());

            return avatarFileName;
        } catch (Exception e) {
            throw new AvatarUploadException("Не удалось загрузить аватар: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteAvatar(HttpServletRequest request) {
        PersonDetails personDetails = ((PersonDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal());
        Person person = personDetails.getPerson();

        String avatarFileName = person.getAvatarFileName();

        if (avatarFileName == null) {
            throw new IllegalOperationException("Ошибка при удалении: аватар не установлен");
        }

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(avatarBucket)
                            .object(avatarFileName)
                            .build()
            );

            person.setAvatarFileName(null);
            personRepository.save(person);

            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());
        } catch (Exception e) {
            throw new AvatarDeleteException("Не удалось удалить аватар: " + e.getMessage());
        }
    }
}
