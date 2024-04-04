package com.example.wellness.dto.common.response;

import com.example.wellness.dto.common.UserDto;
import com.example.wellness.hateos.CustomEntityModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.EntityModel;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@AllArgsConstructor
@Schema(description = "The comment response dto")
public class ResponseWithUserLikesAndDislikesEntity<T> extends ResponseWithUserDtoEntity<T> {
    private List<CustomEntityModel<UserDto>> userLikes;
    private List<CustomEntityModel<UserDto>> userDislikes;

    public ResponseWithUserLikesAndDislikesEntity(CustomEntityModel<T> model, CustomEntityModel<UserDto> user, List<CustomEntityModel<UserDto>> userLikes, List<CustomEntityModel<UserDto>> userDislikes) {
        super(model, user);
        this.userLikes = userLikes;
        this.userDislikes = userDislikes;
    }

}
