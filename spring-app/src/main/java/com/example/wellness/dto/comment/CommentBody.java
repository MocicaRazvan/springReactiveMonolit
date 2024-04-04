package com.example.wellness.dto.comment;

import com.example.wellness.dto.common.generic.TitleBody;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@SuperBuilder
public class CommentBody extends TitleBody {
}
