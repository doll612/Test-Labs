import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class SortingLexicographicOrder {

	public static List<String> sortLexicographically(String[] arr) {
		List<String> newArr = new ArrayList<String>();
		try {
			if (arr != null && arr.length > 0) {
				String aa[] = new String[arr.length];
				for (int i = 0; i < arr.length; i++) {
					for (int j = i + 1; j < arr.length; j++) {
						if (arr[i].compareTo(arr[j]) > 0) {
							String temp = arr[i];
							arr[i] = arr[j];
							arr[j] = temp;
						}
					}
					newArr.add(arr[i]);

				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return newArr;
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter no of elements :");
		int n = Integer.parseInt(sc.nextLine());

		String array[] = new String[n];

		for (int i = 0; i < n; i++) {
			System.out.println("Enter the element " + (i + 1) + " :");
			array[i] = sc.nextLine();
		}
		// ========================Using For loop and if else condition
		
		List<String> sortedArray = sortLexicographically(array);
		System.out.println("Using For Loop and if else : ");
		for (String string : sortedArray) {
			System.out.println(string);
		}

		// ===================Using Collection Frame work===================
		List<String> ss = Arrays.asList(array);
		Collections.sort(ss);
		Iterator<String> it = ss.iterator();
		System.out.println("Using Collection framework : ");
		while (it.hasNext()) {
			System.out.println(it.next());
		}

	}

}
