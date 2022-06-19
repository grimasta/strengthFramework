package com.example.inj.Analysis;

import com.example.inj.StrategyFactory.VectorFusionStrategy.IVectorFusionStrategy;
import com.example.inj.model.storage.DataRepository;

public class VectorFusionStrategyByMajority implements IVectorFusionStrategy {
    private DataRepository dataRepository;

    private final int majority =
            (int)(dataRepository.getVectorFileView()[0].length *0.5) +1 ;          //FIXME: set desire majority level
    public VectorFusionStrategyByMajority(DataRepository dataRepository) {
        this.dataRepository = DataRepository.getInstance();
    }

    @Override
    public void fuseVector() {
        int[][] vector=dataRepository.getVectorFileView();
        int[] fusedVector= dataRepository.getFusedVector();
        int rowSize= vector.length;
        int colSize= vector[0].length;


        for(int i=0; i<rowSize; i++){
            int upCount=0;
            int sameCount=0;

            for(int j=0; j<colSize; j++){
                if(vector[i][j] == 3){
                    upCount++;
                }else if(vector[i][j] == 2){
                    sameCount++;
                }
            }


            if (upCount >=majority){
                fusedVector[i] = 2;
            }else if(sameCount >=majority){
                fusedVector[i] = 1;
            }else{
                fusedVector[i] = 0;
            }
        }

        dataRepository.setFusedVector(fusedVector);
    }
}
