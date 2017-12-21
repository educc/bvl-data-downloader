package com.ecacho.extnmo;

public class App {

    public static void main(String[] args) throws Exception {
        if( args.length == 0){
            System.out.println("Faltan parametros");
            System.out.println("extnmo nemonicoBVL");
            System.exit(-1);
        }

        String result = BVLData.getRazonSocialFromNemonico(args[0]);
        System.out.println(result);
    }
}
