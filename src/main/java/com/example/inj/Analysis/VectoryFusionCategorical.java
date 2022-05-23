package com.example.inj.Analysis;

import com.example.inj.StrategyFactory.VectorFusionStrategy.IVectorFusionStrategy;
import com.example.inj.model.storage.DataRepository;

public class VectoryFusionCategorical implements IVectorFusionStrategy {
    private DataRepository dataRepository;
    private int majority =
            (int)(dataRepository.getVector()[0].length *0.5) +1 ;

    public VectoryFusionCategorical(DataRepository dataRepository) {
        this.dataRepository = DataRepository.getInstance();
    }

    @Override
    public void fuseVector() {
        int[][] vector=dataRepository.getVector();
        int[] fusedVector= dataRepository.getFusedVector();
        int rowSize= vector.length;
        int colSize= vector[0].length;

        for(int i=0; i<rowSize; i++){
            int upCount=0;
            int sameCount=0;

            /*     metric                  index in vector array
            project_LOC_change_ROC,             0
            project_LOC_change_percentage,      1
            file_LOC_change_ROC,                2
            file_LOC_change_percentage,         3
            file_proj_LOC_ratio,                4
            file_proj_LOC_change_ratio          5
            */

            if(vector[i][0] == 2  && vector[i][1] == 2){
                upCount++;
            }else if(vector[i][0] == 1  && vector[i][1] == 1){
                sameCount++;
            }

            if(vector[i][2] == 2  && vector[i][3] == 2){
                upCount++;
            }else if(vector[i][2] == 1  && vector[i][3] == 1){
                sameCount++;
            }

            if(vector[i][4] == 2  && vector[i][5] == 2){
                upCount++;
            }else if(vector[i][4] == 1  && vector[i][5] == 1){
                sameCount++;
            }


            if (upCount >=majority/2){
                fusedVector[i] = 2;
            }else if(sameCount >=majority/2){
                fusedVector[i] = 1;
            }else{
                fusedVector[i] = 0;
            }
        }
        dataRepository.setFusedVector(fusedVector);
    }
}
