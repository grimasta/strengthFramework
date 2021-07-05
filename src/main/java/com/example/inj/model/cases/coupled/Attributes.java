package com.example.inj.model.cases.coupled;

import com.univocity.parsers.annotations.Parsed;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class Attributes {
    @Parsed
    String CommitID;
    @Parsed
    String Source_File_ID;
    @Parsed
    String Destination_File_ID;
    @Parsed
    int Calls;

}
