package com.habts.routine.treinos;
public class Teste {
    public static void main(String[] args) {
        int[] nums = {5, 2, 10, 1};
        int resultado = maiorNUmero(nums);
        System.out.println(resultado);
    }

    public static int maiorNUmero(int[] nums) {
        int maior = Integer.MIN_VALUE;
        for (int i = 0; i < nums.length; i++) {
             int contador = nums[i];
            if (maior < contador) {
                maior = contador;
            }
        }
        return maior;
    }

    public static int segundoMaiorNUmero(int[] nums) {
        int maior = Integer.MIN_VALUE;
        int segundo = Integer.MIN_VALUE;
        for (int i = 0; i < nums.length; i++) {
            int contador = nums[i];
            if (segundo < contador) {
                segundo = contador;
                if(segundo > maior){
                    maior = segundo;
                }
            }

        }
        return segundo;
    }
}

// {5, 2, 10, 1}