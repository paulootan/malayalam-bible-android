package com.jeesmon.malayalambible;

public class ComplexCharacterMapper {
	private static int malayalamUnicodeStart = 3328;
	private static int malayalamUnicodeEnd = 3455;
	private static boolean fixRendering = true;
	private static boolean fixLigatureOnly = false;

	private static final String[][] mappings = new String[][] {
		//ligature mappings to PUA glyphs
		{ "\u0d4d\u0d30", "\uf301" }, 
		{ "\u0d15\u0d4d\u0d15", "\uf306" },
		{ "\u0d15\u0d4d\u0d24", "\uf30b" },
		{ "\u0d15\u0d4d\u0d37", "\uf311" },
		{ "\u0d17\u0d4d\u0d26", "\uf317" },
		{ "\u0d17\u0d4d\u0d28", "\uf31c" },
		{ "\u0d17\u0d4d\u0d2e", "\uf31f" },
		{ "\u0d19\u0d4d\u0d15", "\uf323" },
		{ "\u0d19\u0d4d\u0d19", "\uf327" },
		{ "\u0d1a\u0d4d\u0d1a", "\uf329" },
		{ "\u0d1c\u0d4d\u0d1c", "\uf331" },
		{ "\u0d1e\u0d4d\u0d1a", "\uf339" },
		{ "\u0d1e\u0d4d\u0d1e", "\uf342" },
		{ "\u0d1f\u0d4d\u0d1f", "\uf344" },
		{ "\u0d23\u0d4d\u0d1f", "\uf34c" },
		{ "\u0d24\u0d4d\u0d24", "\uf357" },
		{ "\u0d24\u0d4d\u0d25", "\uf35c" },
		{ "\u0d24\u0d4d\u0d2d", "\uf360" },
		{ "\u0d24\u0d4d\u0d2e", "\uf364" },
		{ "\u0d24\u0d4d\u0d38", "\uf36b" },
		{ "\u0d26\u0d4d\u0d26", "\uf36d" },
		{ "\u0d26\u0d4d\u0d27", "\uf36d" },
		{ "\u0d28\u0d4d\u0d24", "\uf377" },
		{ "\u0d28\u0d4d\u0d26", "\uf37c" },
		{ "\u0d28\u0d4d\u0d27", "\uf37F" },
		{ "\u0d28\u0d4d\u0d28", "\uf382" },
		{ "\u0d2e\u0d4d\u0d2a", "\uf387" },
		{ "\u0d28\u0d4d\u0d2e", "\uf38a" },
		{ "\u0d7b\u0d4d\u0d31", "\uf38e" },
		{ "\u0d2a\u0d4d\u0d2a", "\uf390" },
		{ "\u0d2c\u0d4d\u0d2c", "\uf397" },
		{ "\u0d2e\u0d4d\u0d2e", "\uf39e" },
		{ "യ്യ", "\uf3a1" },
		{ "\u0d36\u0d4d\u0d1a", "\uf3ac" },
		{ "\u0d39\u0d4d\u0d28", "\uf3ba" },
		{ "\u0d39\u0d4d\u0d2e", "\uf3bd" },
		{ "\u0d33\u0d4d\u0d33", "\uf3c0" },
		{ "\u0d31\u0d4d\u0d31", "\uf3c1" },
		{ "\u0d4d\u0d2f", "\uf3c3" },
		{ "(.)\u0d4d\u0d38", "\uf3d0$1" },
		{ "\u0d4d\u0d25", "\uf3c5" },
		{ "(.)\u0d4d\u0d17", "\uf3c6$1" },
		{ "(.)\u0d4d\u0d24", "\uf3c7$1" },
		{ "(.)\u0d4d\u0d27", "\uf3c8$1" },
		{ "(.)\u0d4d\u0d28", "\uf3c9$1" },
		{ "(.)\u0d4d\u0d2a", "\uf3ca$1" },
		{ "(.)\u0d4d\u0d2e", "\uf3cb$1" },
		{ "(.)\u0d4d\u0d36", "\uf3cc$1" },
		{ "(.)\u0d4d\u0d32", "\uf3ce$1" },
		{ "(.)\u0d4d\u0d15", "\uf3cd$1" },
		{ "(.)\u0d4d\u0d1f", "\uf3cf$1" },
		{ "(.)\u0d4d\u0d23", "\uf3de$1" },
		{ "ന്റെ", "\uf38e\u0d46" },
		{ "വ്വ", "\uf3a8" },
		{ "(.)്വ", "$1\uf3c4" },
		
		//re-mappings for display
		{ "([\uf3c6-\uf3d0]*.)\uf301", "\uf301$1" },
		{ "([\uf3c6-\uf3d0]*.)\u0d46", "\u0d46$1" },
		{ "([\uf3c6-\uf3d0]*.)\u0d47", "\u0d47$1" },
		{ "([\uf3c6-\uf3d0]*.)\u0d4a", "\u0d46$1\u0d3e" },
		{ "([\uf3c6-\uf3d0]*.)\uf301\u0d4a", "\u0d46\uf301$1\u0d3e" },
		{ "\uf301\u0d46", "\u0d46\uf301" },
		{ "([\uf3c6-\uf3d0]*.)\u0d4b", "\u0d47$1\u0d3e" },
		{ "([\uf3c6-\uf3d0]*.)\uf301\u0d4b", "\u0d47\uf301$1\u0d3e" },
		{ "\uf301\u0d47", "\u0d47\uf301" },
		{ "([\uf3c6-\uf3d0]*.)\u0d4c", "\u0d46$1\u0d57" },
		{ "([\uf3c6-\uf3d0]*.)\u0d48", "\u0d46\u0d46$1" },
		{ "(.)([\u0d46-\u0d47])([\uf3c3-\uf3c5])", "$2$1$3" },
	};
	
	private static final String[][] ligatureMappings = new String[][] {
		//ligature mappings to PUA glyphs
		{ "\u0d4d\u0d30", "\uf301" }, 
		{ "\u0d15\u0d4d\u0d15", "\uf306" },
		{ "\u0d15\u0d4d\u0d24", "\uf30b" },
		{ "\u0d15\u0d4d\u0d37", "\uf311" },
		{ "\u0d17\u0d4d\u0d26", "\uf317" },
		{ "\u0d17\u0d4d\u0d28", "\uf31c" },
		{ "\u0d17\u0d4d\u0d2e", "\uf31f" },
		{ "\u0d19\u0d4d\u0d15", "\uf323" },
		{ "\u0d19\u0d4d\u0d19", "\uf327" },
		{ "\u0d1a\u0d4d\u0d1a", "\uf329" },
		{ "\u0d1c\u0d4d\u0d1c", "\uf331" },
		{ "\u0d1e\u0d4d\u0d1a", "\uf339" },
		{ "\u0d1e\u0d4d\u0d1e", "\uf342" },
		{ "\u0d1f\u0d4d\u0d1f", "\uf344" },
		{ "\u0d23\u0d4d\u0d1f", "\uf34c" },
		{ "\u0d24\u0d4d\u0d24", "\uf357" },
		{ "\u0d24\u0d4d\u0d25", "\uf35c" },
		{ "\u0d24\u0d4d\u0d2d", "\uf360" },
		{ "\u0d24\u0d4d\u0d2e", "\uf364" },
		{ "\u0d24\u0d4d\u0d38", "\uf36b" },
		{ "\u0d26\u0d4d\u0d26", "\uf36d" },
		{ "\u0d26\u0d4d\u0d27", "\uf36d" },
		{ "\u0d28\u0d4d\u0d24", "\uf377" },
		{ "\u0d28\u0d4d\u0d26", "\uf37c" },
		{ "\u0d28\u0d4d\u0d27", "\uf37F" },
		{ "\u0d28\u0d4d\u0d28", "\uf382" },
		{ "\u0d2e\u0d4d\u0d2a", "\uf387" },
		{ "\u0d28\u0d4d\u0d2e", "\uf38a" },
		{ "\u0d7b\u0d4d\u0d31", "\uf38e" },
		{ "\u0d2a\u0d4d\u0d2a", "\uf390" },
		{ "\u0d2c\u0d4d\u0d2c", "\uf397" },
		{ "\u0d2e\u0d4d\u0d2e", "\uf39e" },
		{ "യ്യ", "\uf3a1" },
		{ "\u0d36\u0d4d\u0d1a", "\uf3ac" },
		{ "\u0d39\u0d4d\u0d28", "\uf3ba" },
		{ "\u0d39\u0d4d\u0d2e", "\uf3bd" },
		{ "\u0d33\u0d4d\u0d33", "\uf3c0" },
		{ "\u0d31\u0d4d\u0d31", "\uf3c1" },
		{ "\u0d4d\u0d2f", "\uf3c3" },
		{ "(.)\u0d4d\u0d38", "\uf3d0$1" },
		{ "\u0d4d\u0d25", "\uf3c5" },
		{ "(.)\u0d4d\u0d17", "\uf3c6$1" },
		{ "(.)\u0d4d\u0d24", "\uf3c7$1" },
		{ "(.)\u0d4d\u0d27", "\uf3c8$1" },
		{ "(.)\u0d4d\u0d28", "\uf3c9$1" },
		{ "(.)\u0d4d\u0d2a", "\uf3ca$1" },
		{ "(.)\u0d4d\u0d2e", "\uf3cb$1" },
		{ "(.)\u0d4d\u0d36", "\uf3cc$1" },
		{ "(.)\u0d4d\u0d32", "\uf3ce$1" },
		{ "(.)\u0d4d\u0d15", "\uf3cd$1" },
		{ "(.)\u0d4d\u0d1f", "\uf3cf$1" },
		{ "(.)\u0d4d\u0d23", "\uf3de$1" },
		{ "ന്റെ", "\uf3d9\uf38e" },
		{ "വ്വ", "\uf3a8" },
		{ "്വ", "\uf3c4" },
		{ "([\uf301-\uf3d0])\u0d02", "$1\uf3d1" },
		{ "([\uf301-\uf3d0])\u0d03", "$1\uf3d2" },
		{ "([\uf301-\uf3d0])\u0d3e", "$1\uf3d3" },
		{ "([\uf301-\uf3d0])\u0d3f", "$1\uf3d4" },
		{ "([\uf301-\uf3d0])\u0d40", "$1\uf3d5" },
		{ "([\uf301-\uf3d0])\u0d41", "$1\uf3d6" },		
		{ "([\uf301-\uf3d0])\u0d42", "$1\uf3d7" },
		{ "([\uf301-\uf3d0])\u0d43", "$1\uf3d8" },
		{ "([\uf301-\uf3d0])\u0d4d", "$1\uf3dc" },
		{ "([\uf301-\uf3d0])\u0d57", "$1\uf3dd" },
		{ "([\uf301-\uf3d0])\u0d46", "\uf3d9$1" },
		{ "([\uf301-\uf3d0])\u0d47", "\uf3da$1" },
		{ "([\uf301-\uf3d0])\u0d48", "\uf3db$1" },
		{ "([\uf301-\uf3d0])\u0d4a", "\uf3d9$1\uf3d3" },
		{ "([\uf301-\uf3d0])\u0d4b", "\uf3da$1\uf3d3" },
				
		{ "(.)([\uf3d9])([\uf3c3-\uf3c5])", "\uf3d9$1$3" },
		{ "(.)([\uf3da])([\uf3c3-\uf3c5])", "\uf3da$1$3" },
		{ "(.)([\uf3db])([\uf3c3-\uf3c5])", "\uf3db$1$3" },
		{ "([\uf3d3-\uf3d8])\u0d02", "$1\uf3d1" },
		{ "([\uf3d3-\uf3d8])\u0d03", "$1\uf3d2" },
				
		{ "(.)\uf301", "\uf301$1" },
		
		{ "(.)\u0d46\u0d3e", "\uf3d9$1\uf3d3" },
		{ "(.)\u0d47\u0d3e", "\uf3da$1\uf3d3" },
		
		{ "([\uf301-\uf3d0])\u0d3e", "$1\uf3d3" },
		{ "([\uf301-\uf3d0])\u0d3f", "$1\uf3d4" },
		{ "([\uf301-\uf3d2])\u0d40", "$1\uf3d5" },
		{ "([\uf301-\uf3d2])\u0d41", "$1\uf3d6" },
		{ "([\uf301-\uf3d2])\u0d42", "$1\uf3d7" },
		{ "([\uf301-\uf3d2])\u0d43", "$1\uf3d8" },
		
		{ "([\uf3c6-\uf3d0])\uf301(.)", "\uf301$1$2" },
		{ "([\uf3c6-\uf3d0])(.)\u0d46", "\uf3d9$1$2" },
		{ "([\uf3c6-\uf3d0])(.)\u0d47", "\uf3da$1$2" },
		{ "([\uf3c6-\uf3d0])(.)\u0d48", "\uf3db$1$2" },
	};

	public static String fix(String text) {
		if(!fixRendering) {
			return text;
		}
		
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) >= malayalamUnicodeStart
					&& text.charAt(i) <= malayalamUnicodeEnd) {
				
				if(fixLigatureOnly) {
					for (int j = 0; j < ligatureMappings.length; j++) {
						text = text.replaceAll(ligatureMappings[j][0], ligatureMappings[j][1]);
					}
				}
				else {
					for (int j = 0; j < mappings.length; j++) {
						text = text.replaceAll(mappings[j][0], mappings[j][1]);
					}
				}
				break;
			}
		}

		return text;
	}
}
