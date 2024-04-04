package com.example.wellness.dto.test;

import com.example.wellness.models.user.UserCustom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostFull {
    private Long id;
    private List<String> tags;
    private String title;
    private String body;
    private List<UserCustom> userLikes;
    private List<UserCustom> userDislikes;
    private UserCustom user;
}
