package ucab.tesis.sokoban.logic;

import java.util.HashSet;
import java.util.Set;

public class Lexic {

	static private String[] lexicRules = {"0","1","2","3","4","5","6","7","8","9"};
	
	public static boolean lexicParse(String lex){
	
		Set<String> a = new HashSet<String>(), b = new HashSet<String>();
		
		for (String rule : lexicRules) {
			a.add(rule);
		}
		
		for (int i = 0; i < lex.length(); i++) {
			b.add(""+lex.charAt(i));
		}
		
		return a.containsAll(b);
		
	}
	
	/*
	public static void main (String[] args){
		
		System.out.println(lexicParse("0706111000141H000101111100001120301100001111111"));
		System.out.println(lexicParse("191100111100000001001000000013011100000100001000111011011111003003030110101100001101011330011010000111110001111000111010000000140100000001101000000011011000000100010000001000100000012221000000122210000001111100000"));
		
	}
	*/
}
