package com.example.producer.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LXJ
 * @description
 * @date 2020/3/2 17:58
 */

@Data
public class Order implements Serializable{
    private String id;
    private String content;
    private String name;

}
