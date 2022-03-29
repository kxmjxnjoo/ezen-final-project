package com.ezen.springfeed.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;

@Mapper
public interface IPostDao {
    void selectPost(HashMap<String, Object> paramMap);
    void insertPost(HashMap<String, Object> paramMap);
    void updatePost(HashMap<String, Object> paramMap);
    void deletePost(HashMap<String, Object> paramMap);
}