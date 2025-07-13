package oreumi.group2.carrotClone.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReadReceiptDTO {

    private String readerUsername;
    private List<Long> messageIds; // 읽음 처리할 메세지 ID
}