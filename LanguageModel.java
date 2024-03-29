import java.util.HashMap;
import java.util.Random;


public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
        char charachter;
        String window = "";
        In in = new In(fileName);

        for (int i = 0; i < windowLength; i++) {
            window +=  in.readChar();
        }
        while (!(in.isEmpty())) {
            charachter = in.readChar();
            List probs = CharDataMap.get(window);
            if (probs == null){
                probs = new List();
                CharDataMap.put(window, probs);
            }
            probs.update(charachter);
            window += charachter;
            window = window.substring(1);
        }
        for (List probs : CharDataMap.values())
            calculateProbabilities(probs);
	}
	

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	public void calculateProbabilities(List probs) {
        int sizeP =	probs.getSize() ;			
        int charsNum = 0;
        for (int i = 0; i < sizeP ; i++){
            charsNum +=  probs.get(i).count;
        }
        CharData first = probs.get(0);
        Double firstP = first.count / (double)charsNum;
        first.p = firstP;
        first.cp = firstP;
  
        CharData prev = first;
        CharData current = null;
          for (int j = 1 ; j < probs.getSize() ; j++) {
           current = probs.get(j);
           double x = current.count / (double)charsNum;
           current.p = x;
           current.cp = prev.cp + x;
           prev = current;
          }
	}

    // Returns a random character from the given probabilities list.
	public char getRandomChar(List probs) {
		double random = randomGenerator.nextDouble();
        int listS = probs.getSize();
        
        for (int i = 0; i < listS; i++) {
            CharData currentCharData = probs.get(i);
            if (currentCharData.cp > random) {
                return  currentCharData.chr;
            }
        }
        return probs.get(listS -1).chr;
	}
	

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
        if (initialText.length() < windowLength){
            return initialText;
        }
        String window = initialText.substring(initialText.length() - windowLength);
        String gen = window;
        for (int i = 0; i < textLength; i++) {
            List probs = CharDataMap.get(window);

            if(probs != null){
                char newC = getRandomChar(probs);
                gen += newC;
                window = gen.substring(gen.length() - windowLength);
            }
            else {
                return gen;
            }
        }
        return gen;
	}
	

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {
    // Create a new language model with a window length of 3
    //LanguageModel model = new LanguageModel(3);

    // Train the model on a text file
    //model.train(fileName);

    // Generate random text
    // String generatedText = model.generate("initial text", 100);

    // Print the generated text
    
}

    }

