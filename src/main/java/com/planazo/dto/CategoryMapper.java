package com.planazo.dto;

import com.planazo.dto.response.CategoryResponse;
import com.planazo.model.Category;

public class CategoryMapper {

    public static CategoryResponse toCategoryResponse(Category categoria) {
        if (categoria == null) {
            return null;
        }

        return CategoryResponse.builder()
                .id(categoria.getId())
                .name(categoria.getName())
                .description(categoria.getDescription())
                .iconEmoji(categoria.getIconEmoji())
                .colorHex(categoria.getColorHex())
                .active(categoria.getActive())
                .build();
    }
}
