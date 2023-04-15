package com.example.medicalrecordsproject.utils;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MapperUtil {
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    public <S, T> List<T> mapList(List<S> sourceList, Class<T> target) {
        return sourceList
                .stream()
                .map(item -> modelMapper().map(item, target))
                .collect(Collectors.toList());
    }
}
