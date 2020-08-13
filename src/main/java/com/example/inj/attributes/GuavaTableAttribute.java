package com.example.inj.attributes;

import lombok.*;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GuavaTableAttribute {

    private Map<String, String> commitToADateMap= new LinkedHashMap<>();
    private Map<String, String> commitToDateMap= new LinkedHashMap<>();

    private List<Map<String, Map<String, String>>> lastCommitIDForAB= new LinkedList<>();
}
