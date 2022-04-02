package com.example.inj.readingStrategy.strategy;

import com.example.inj.attributes.AttributesField;
import com.example.inj.model.storage.DataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.common.collect.Table;
import lombok.*;
import tech.tablesaw.api.Table;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TestReadingStrategy implements TestIReadingStrategy{

    //Tablesaw(Java's pandas) table instead of guava table,
    private Table df;

    //Map<FileId, PerCommitDetail>
    private Map<String, List<Object>> fileDetails;

    //Map<CommitId,FileIdParticipate>
    private Map<String, List<Object>> commitDetails;
    //private DataRepository dataRepository;
    //private LinkedHashMap<> commitOrder;
    //private LinkedList<String> file
    @Override
    public void parseData() throws IOException, ParseException {
        Table dataFrame = Table.read().csv("C:\\Users\\Rongji He\\Desktop\\data\\elisaFinalVersion.csv");

        fileDetails= new LinkedHashMap<>();
        commitDetails= new LinkedHashMap<>();
        //columns that are unimportant/unrelated
        /*dataFrame.removeColumns("authored_at","commit_additions","commit_deletions",
                "changed_files","is_bug_linked","is_fix_related",
                "is_merge_commit","is_refactoring","file_path",
                "previous_file_path","file_additions","file_deletions",
                "fractal_value","fractal_value_over_lines","distinct_authors_to_now"
        );*/

        //populate data structure
        for (int i = 0; i < dataFrame.rowCount(); i++) {
            String commitId = dataFrame.stringColumn("id").get(i);
            String file_id = dataFrame.stringColumn("file_id").get(i);
            boolean is_bug_fixing= dataFrame.booleanColumn("is_bug_fixing").get(i);

            double project_LOC_change_ROC =dataFrame.doubleColumn("project_LOC_change_ROC").get(i);
            double project_LOC_change_percentage =dataFrame.doubleColumn("project_LOC_change_percentage").get(i);

            double file_LOC_change_ROC =dataFrame.doubleColumn("file_LOC_change_ROC").get(i);
            double file_LOC_change_percentage =dataFrame.doubleColumn("file_LOC_change_percentage").get(i);
            double file_proj_LOC_change_rito= dataFrame.doubleColumn("file_proj_LOC_change_rito").get(i);

            List<Object> commitData= new ArrayList<>();
            commitData.add(is_bug_fixing);
            commitData.add(project_LOC_change_ROC);
            commitData.add(project_LOC_change_percentage);
            commitDetails.put(commitId, commitData);

            List<Object> fileData= new ArrayList<>();
            fileData.add(is_bug_fixing);
            fileData.add(file_LOC_change_ROC);
            fileData.add(file_LOC_change_percentage);
            fileData.add(file_proj_LOC_change_rito);
            fileDetails.put(file_id,fileData);

            //LocalDate d = dataFrame.dateColumn("d").get(i);
            //System.out.println(s + " happened on " + d);
        }
    }


}
