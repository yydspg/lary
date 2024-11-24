package cn.lary.stream.vo;


import lombok.Data;

@Data
public class RoomVO extends HomeVO{

    private int category;

    private long rid;

    private String title;

    private int ad;

    private String external;

}
