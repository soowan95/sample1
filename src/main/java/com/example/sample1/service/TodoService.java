package com.example.sample1.service;

import com.example.sample1.dao.TodoDao;
import com.example.sample1.domain.Todo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class TodoService {

  private final TodoDao dao;
  private final S3Client s3Client;
  @Value("${aws.bucketName}")
  private String bucketName;


  public List<Todo> list() {
    return dao.list();
  }

  public boolean insert(Todo todo, MultipartFile[] files) throws IOException {
    int count = dao.insert(todo);

    if (files != null && files.length > 0) {
      for (MultipartFile file : files) {
        if (file.getSize() > 0) {
          String key = "sample1/" + todo.getId() + "/" + file.getOriginalFilename();
          PutObjectRequest request = PutObjectRequest.builder()
                  .key(key)
                  .bucket(bucketName)
                  .acl(ObjectCannedACL.PUBLIC_READ)
                  .build();

          s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        }
      }
    }

    return count == 1;
  }
}
