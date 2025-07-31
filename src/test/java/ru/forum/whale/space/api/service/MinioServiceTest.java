package ru.forum.whale.space.api.service;

import io.minio.*;
import io.minio.http.Method;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.forum.whale.space.api.exception.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static javax.script.ScriptEngine.FILENAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static ru.forum.whale.space.api.util.TestUtil.createMockMultipartFile;

@ExtendWith(MockitoExtension.class)
class MinioServiceTest {
    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private MinioService minioService;

    @Captor
    private ArgumentCaptor<List<String>> filesCaptor;

    private static final String BUCKET_NAME = "bucket";
    private static final String FOLDER = "folder";

    private static final int MIN_IMAGE_WIDTH = 150;
    private static final int MIN_IMAGE_HEIGHT = 150;

    @Test
    void initBucket_whenBucketExistsThrowsException_thenThrowGeneralMinioException() throws Exception {
        RuntimeException e1 = new RuntimeException();

        when(minioClient.bucketExists(createBucketExistsArgs())).thenThrow(e1);

        GeneralMinioException e2 = assertThrows(GeneralMinioException.class,
                () -> minioService.initBucket(BUCKET_NAME));

        verify(minioClient, never()).makeBucket(any(MakeBucketArgs.class));

        assertEquals("Ошибка при инициализации MinIO бакета: " + e1.getMessage(), e2.getMessage());
    }

    @Test
    void initBucket_whenMakeExistsThrowsException_thenThrowGeneralMinioException() throws Exception {
        RuntimeException e1 = new RuntimeException();

        when(minioClient.bucketExists(createBucketExistsArgs())).thenReturn(false);
        doThrow(e1).when(minioClient).makeBucket(createMakeBucketArgs());

        GeneralMinioException e2 = assertThrows(GeneralMinioException.class,
                () -> minioService.initBucket(BUCKET_NAME));

        assertEquals("Ошибка при инициализации MinIO бакета: " + e1.getMessage(), e2.getMessage());
    }


    @Test
    void initBucket_whenBucketExist_thenSkipCreation() throws Exception {
        when(minioClient.bucketExists(createBucketExistsArgs())).thenReturn(true);

        minioService.initBucket(BUCKET_NAME);

        verify(minioClient, never()).makeBucket(any(MakeBucketArgs.class));
    }

    @Test
    void initBucket_whenBucketDoesNotExists_thenCreateBucket() throws Exception {
        when(minioClient.bucketExists(createBucketExistsArgs())).thenReturn(false);

        minioService.initBucket(BUCKET_NAME);

        verify(minioClient).makeBucket(createMakeBucketArgs());
    }

    @Test
    void generatePresignedUrl_whenStatObjectThrowsException_thenThrowResourceNotFoundException() throws Exception {
        RuntimeException e1 = new RuntimeException();

        when(minioClient.statObject(createStatObjectArgs())).thenThrow(e1);

        ResourceNotFoundException e2 = assertThrows(ResourceNotFoundException.class,
                () -> minioService.generatePresignedUrl(BUCKET_NAME, FILENAME));

        verify(minioClient, never()).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));

        assertEquals("Файл '%s' не найден".formatted(FILENAME), e2.getMessage());
    }

    @Test
    void generatePresignedUrl_whenGetPresignedObjectUrlThrowsException_thenThrowGeneralMinioException()
            throws Exception {
        RuntimeException e1 = new RuntimeException();

        when(minioClient.getPresignedObjectUrl(createGetPresignedObjectUrlArgs())).thenThrow(e1);

        GeneralMinioException e2 = assertThrows(GeneralMinioException.class,
                () -> minioService.generatePresignedUrl(BUCKET_NAME, FILENAME));

        verify(minioClient).statObject(createStatObjectArgs());

        assertEquals("Ошибка при генерации временной ссылки на файл '%s': %s".formatted(FILENAME, e1.getMessage()),
                e2.getMessage());
    }

    @Test
    void generatePresignedUrl_whenObjectExists_thenReturnGeneratedPresignedUrl() throws Exception {
        String filePresignedUrl = "url";

        when(minioClient.getPresignedObjectUrl(createGetPresignedObjectUrlArgs())).thenReturn(filePresignedUrl);

        String result = minioService.generatePresignedUrl(BUCKET_NAME, FILENAME);

        verify(minioClient).statObject(createStatObjectArgs());

        assertEquals(filePresignedUrl, result);
    }

    @Test
    void generatePresignedUrls_thenReturnGeneratedPresignedUrls() throws Exception {
        List<String> fileNames = List.of("file1", "file2");

        AtomicInteger counter = new AtomicInteger(1);

        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenAnswer(invocation -> "file" + counter.getAndIncrement());

        List<String> result = minioService.generatePresignedUrls(BUCKET_NAME, fileNames);

        assertEquals(fileNames, result);
    }

    @Test
    void uploadImages_whenThrownIllegalOperationException_thenDeleteUploadedFilesAndRethrows() {
        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);

        List<MultipartFile> files = List.of(file1, file2);

        IllegalOperationException e1 = new IllegalOperationException("error");

        MinioService spyMinioService = spy(minioService);

        byte[] imageBytes = new byte[0];

        doReturn(imageBytes).when(spyMinioService)
                .validateImage(file1, MinioService.MIN_IMAGE_WIDTH, MinioService.MIN_IMAGE_HEIGHT);
        doThrow(e1).when(spyMinioService)
                .validateImage(file2, MinioService.MIN_IMAGE_WIDTH, MinioService.MIN_IMAGE_HEIGHT);
        doNothing().when(spyMinioService)
                .loadFile(eq(BUCKET_NAME), anyString(), any(MultipartFile.class), eq(imageBytes));
        doNothing().when(spyMinioService).deleteFiles(eq(BUCKET_NAME), anyList());

        IllegalOperationException e2 = assertThrows(IllegalOperationException.class,
                () -> spyMinioService.uploadImages(BUCKET_NAME, files, FOLDER));

        verify(spyMinioService, times(1))
                .loadFile(eq(BUCKET_NAME), anyString(), any(MultipartFile.class), any(byte[].class));
        verify(spyMinioService).deleteFiles(eq(BUCKET_NAME), filesCaptor.capture());

        List<String> deletedFiles = filesCaptor.getValue();

        assertEquals(1, deletedFiles.size());
        assertEquals(e1, e2);
    }

    @Test
    void uploadImages_whenThrownNotIllegalOperationException_thenDeleteUploadedFilesAndThrowMinioUploadException() {
        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);

        List<MultipartFile> files = List.of(file1, file2);

        RuntimeException e1 = new RuntimeException();

        MinioService spyMinioService = spy(minioService);

        doReturn(new byte[0]).when(spyMinioService).validateImage(any(MultipartFile.class),
                eq(MinioService.MIN_IMAGE_WIDTH), eq(MinioService.MIN_IMAGE_HEIGHT));
        doNothing().when(spyMinioService).loadFile(eq(BUCKET_NAME), anyString(), eq(file1), any(byte[].class));
        doThrow(e1).when(spyMinioService).loadFile(eq(BUCKET_NAME), anyString(), eq(file2), any(byte[].class));
        doNothing().when(spyMinioService).deleteFiles(eq(BUCKET_NAME), anyList());

        MinioUploadException e2 = assertThrows(MinioUploadException.class,
                () -> spyMinioService.uploadImages(BUCKET_NAME, files, FOLDER));

        verify(spyMinioService, times(2)).validateImage(any(MultipartFile.class),
                eq(MinioService.MIN_IMAGE_WIDTH), eq(MinioService.MIN_IMAGE_HEIGHT));
        verify(spyMinioService).deleteFiles(eq(BUCKET_NAME), filesCaptor.capture());

        List<String> deletedFiles = filesCaptor.getValue();

        assertEquals(1, deletedFiles.size());
        assertEquals("Ошибка загрузки файлов: " + e1.getMessage(), e2.getMessage());
    }

    @Test
    void uploadImages_thenReturnUploadedFileNames() {
        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);

        List<MultipartFile> files = List.of(file1, file2);

        MinioService spyMinioService = spy(minioService);

        doReturn(new byte[0]).when(spyMinioService).validateImage(any(MultipartFile.class),
                eq(MinioService.MIN_IMAGE_WIDTH), eq(MinioService.MIN_IMAGE_HEIGHT));
        doNothing().when(spyMinioService)
                .loadFile(eq(BUCKET_NAME), anyString(), any(MultipartFile.class), any(byte[].class));

        List<String> fileNames = spyMinioService.uploadImages(BUCKET_NAME, files, FOLDER);

        verify(spyMinioService, times(2)).validateImage(any(MultipartFile.class),
                eq(MinioService.MIN_IMAGE_WIDTH), eq(MinioService.MIN_IMAGE_HEIGHT));
        verify(spyMinioService, times(2))
                .loadFile(eq(BUCKET_NAME), anyString(), any(MultipartFile.class), any(byte[].class));
        verify(spyMinioService, never()).deleteFiles(eq(BUCKET_NAME), anyList());

        assertEquals(files.size(), fileNames.size());
        assertTrue((fileNames.getFirst() + "/").contains(FOLDER));
        assertTrue((fileNames.getLast() + "/").contains(FOLDER));
    }

    @Test
    void deleteFile_whenRemoveObjectThrowsException_thenThrowMinioDeletionException() throws Exception {
        RuntimeException e1 = new RuntimeException();

        doThrow(e1).when(minioClient).removeObject(createRemoveObjectArgs(FILENAME));

        MinioDeleteException e2 = assertThrows(MinioDeleteException.class,
                () -> minioService.deleteFile(BUCKET_NAME, FILENAME));

        assertEquals("Не удалось удалить файл '%s': %s".formatted(FILENAME, e1.getMessage()), e2.getMessage());
    }

    @Test
    void deleteFile_thenDeleteFile() throws Exception {
        minioService.deleteFile(BUCKET_NAME, FILENAME);

        verify(minioClient).removeObject(createRemoveObjectArgs(FILENAME));
    }

    @Test
    void deleteFiles_whenRemoveObjectThrowsExceptionForOneFile_thenDeleteOthers() throws Exception {
        List<String> fileNames = List.of("file1", "file2", "file3");

        RuntimeException e1 = new RuntimeException();

        doThrow(e1).when(minioClient).removeObject(createRemoveObjectArgs(fileNames.get(1)));

        minioService.deleteFiles(BUCKET_NAME, fileNames);

        verify(minioClient, times(3)).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void deleteFiles_thenDeleteFiles() throws Exception {
        List<String> fileNames = List.of("file1", "file2", "file3");

        minioService.deleteFiles(BUCKET_NAME, fileNames);

        verify(minioClient, times(3)).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void validateImage_whenFileIsInvalid_thenThrowIllegalOperationException() throws Exception {
        byte[] invalidImageBytes = "not image".getBytes();
        InputStream inputStream = new ByteArrayInputStream(invalidImageBytes);

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getInputStream()).thenReturn(inputStream);

        IllegalOperationException e = assertThrows(IllegalOperationException.class,
                () -> minioService.validateImage(multipartFile, MIN_IMAGE_WIDTH, MIN_IMAGE_HEIGHT));

        assertEquals("Невалидный файл изображения", e.getMessage());
    }

    @Test
    void validateImage_whenImageIsTooSmall_thenThrowIllegalOperationException() throws Exception {
        int realImageWidth = 100;
        int realImageHeight = 100;

        BufferedImage image = new BufferedImage(realImageWidth, realImageHeight, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", imageOutputStream);

        byte[] validImageBytes = imageOutputStream.toByteArray();
        InputStream inputStream = new ByteArrayInputStream(validImageBytes);

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getInputStream()).thenReturn(inputStream);

        IllegalOperationException exception = assertThrows(IllegalOperationException.class,
                () -> minioService.validateImage(multipartFile, MIN_IMAGE_WIDTH, MIN_IMAGE_HEIGHT));

        assertEquals("Минимальный размер изображения — %dx%d пикселей"
                .formatted(MIN_IMAGE_WIDTH, MIN_IMAGE_HEIGHT), exception.getMessage());
    }

    @Test
    void validateImage_thenFileUnreadable_thenThrowIllegalOperationException() throws Exception {
        MultipartFile file = mock(MultipartFile.class);

        IOException e1 = new IOException();

        when(file.getInputStream()).thenThrow(e1);

        IllegalOperationException e2 = assertThrows(IllegalOperationException.class,
                () -> minioService.validateImage(file, MIN_IMAGE_WIDTH, MIN_IMAGE_HEIGHT));

        assertEquals("Файл повреждён или не может быть прочитан: " + e1.getMessage(), e2.getMessage());
    }

    @Test
    void validateImage_thenReturnImageBytes() throws Exception {
        int realImageWidth = 200;
        int realImageHeight = 200;

        BufferedImage image = new BufferedImage(realImageWidth, realImageHeight, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", imageOutputStream);

        byte[] validImageBytes = imageOutputStream.toByteArray();
        InputStream inputStream = new ByteArrayInputStream(validImageBytes);

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getInputStream()).thenReturn(inputStream);

        byte[] result = minioService.validateImage(multipartFile, MIN_IMAGE_WIDTH, MIN_IMAGE_HEIGHT);

        assertArrayEquals(validImageBytes, result);
    }

    @Test
    void loadFile_whenPutObjectThrowsException_thenThrowMinioUploadException() throws Exception {
        MultipartFile file = createMockMultipartFile(IMAGE_PNG_VALUE);

        byte[] bytes = new byte[0];

        RuntimeException e1 = new RuntimeException();

        when(minioClient.putObject(any(PutObjectArgs.class))).thenThrow(e1);

        MinioUploadException e2 = assertThrows(MinioUploadException.class,
                () -> minioService.loadFile(BUCKET_NAME, FILENAME, file, bytes));

        assertEquals("Не удалось загрузить файл '%s': %s".formatted(FILENAME, e1.getMessage()), e2.getMessage());
    }

    @Test
    void loadFile_thenLoadFile() throws Exception {
        MultipartFile file = createMockMultipartFile(IMAGE_PNG_VALUE);

        byte[] bytes = new byte[0];

        minioService.loadFile(BUCKET_NAME, FILENAME, file, bytes);

        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    private BucketExistsArgs createBucketExistsArgs() {
        return BucketExistsArgs.builder()
                .bucket(BUCKET_NAME)
                .build();
    }

    private MakeBucketArgs createMakeBucketArgs() {
        return MakeBucketArgs.builder()
                .bucket(BUCKET_NAME)
                .build();
    }

    private StatObjectArgs createStatObjectArgs() {
        return StatObjectArgs.builder()
                .bucket(BUCKET_NAME)
                .object(FILENAME)
                .build();
    }

    private GetPresignedObjectUrlArgs createGetPresignedObjectUrlArgs() {
        return GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(BUCKET_NAME)
                .object(FILENAME)
                .expiry(12, TimeUnit.HOURS)
                .build();
    }

    private RemoveObjectArgs createRemoveObjectArgs(String fileName) {
        return RemoveObjectArgs.builder()
                .bucket(BUCKET_NAME)
                .object(fileName)
                .build();
    }
}