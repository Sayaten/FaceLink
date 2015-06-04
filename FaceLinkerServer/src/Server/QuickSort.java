package Server;

import java.util.ArrayList;

public class QuickSort {
	public static int partition(ArrayList<ImageSimilarity> arr, int left, int right){
		ImageSimilarity pivot = arr.get((left + right) / 2);
		while(left < right){
			while(arr.get(left).getSimilarity() > pivot.getSimilarity()){
				while ((arr.get(left).getSimilarity() > pivot.getSimilarity()) && (left < right))
					left++;
				while ((arr.get(right).getSimilarity() < pivot.getSimilarity()) && (left < right))
					right--;

				if (left < right) {
					ImageSimilarity temp = arr.get(left);
					arr.set(left, arr.get(right));
					arr.set(right, temp);
				}
			}
		}
		return left;
	}
	
	public static void quickSort(ArrayList<ImageSimilarity> arr, int left, int right){
		if( left < right ){
			int pivot_index = partition(arr, left, right);
			
			quickSort(arr, left, pivot_index - 1);
			quickSort(arr, pivot_index + 1, right);
		}
	}
}
