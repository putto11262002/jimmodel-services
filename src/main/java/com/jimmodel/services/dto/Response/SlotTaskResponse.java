package com.jimmodel.services.dto.Response;

import com.jimmodel.services.domain.Event;
import lombok.Data;

import java.util.UUID;

@Data
public class SlotTaskResponse {
    private UUID id;
    private String title;

    public SlotTaskResponse(Event task){
//        System.out.println(task);
        this.id = task.getId();
        this.title = task.getTitle();
    }
}
