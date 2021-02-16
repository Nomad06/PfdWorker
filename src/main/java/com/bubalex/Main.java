package com.bubalex;

import com.bubalex.aws.AwsS3Service;
import com.bubalex.aws.AwsS3ServiceImpl;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        PdfReader pdfReader = new PdfReader();
        //pdfReader.create();
        pdfReader.read(Paths.get("C:/i-765.pdf"));
        int[] mas = {1, 2, 3, 4, 7, 9, 13, 14, 18, 26};
        int result = Searcher.binarySearch(2, mas, 0, mas.length);
        System.out.println(result);
        boolean isCyclic = CyclicRotation.isCyclicRotation("ACTGACG", "TGACDAC");
        System.out.println(isCyclic);
        AwsS3Service awsS3Service = new AwsS3ServiceImpl();
        //awsS3Service.getObject();
    }
}
