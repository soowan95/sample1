package com.example.sample1.dao;

import com.example.sample1.domain.Todo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Mapper
public interface TodoDao {

  @Select("""
  SELECT t.id, t.todo, t.inserted, COUNT(tf.todoId) numOfFiles
  FROM todoFile tf RIGHT JOIN todo t ON tf.todoId = t.id
  GROUP BY t.id
  ORDER BY id DESC
  """)
  public List<Todo> list();

  @Insert("""
  INSERT INTO todo (todo)
  VALUE (#{todo})
  """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  public int insert(Todo todo);

  @Insert("""
  INSERT INTO todoFile (todoId, name)
  VALUE (#{todo.id}, #{fileName})
  """)
  void insertFile(Todo todo, String fileName);

  @Select("""
  SELECT name
  FROM todoFile
  WHERE todoId = #{todoId}
  """)
  List<String> selectFilesByTodoId(Integer todoId);
}