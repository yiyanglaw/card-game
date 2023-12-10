import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent; // Add this line
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GoBoomGame extends JFrame {
    private List<String> deck;
    private List<String> player1, player2, player3, player4;
    private List<String> center;
    private int currentPlayerIndex;
    private JLabel consoleOutput;
    private JTextField cardInputField;

    private boolean gameOver;  // Add this line

    // Add these variables to your class
    private int player1Score;
    private int player2Score;
    private int player3Score;
    private int player4Score;
    private boolean roundOver; // Add this line



    public GoBoomGame() {
        deck = new ArrayList<>();
        player1 = new ArrayList<>();
        player2 = new ArrayList<>();
        player3 = new ArrayList<>();
        player4 = new ArrayList<>();
        center = new ArrayList<>();
        currentPlayerIndex = 0;
        roundOver = false; // Add this line
        gameOver = false;  // Add this line

        // Move the declaration here
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        setTitle("Go Boom Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        consoleOutput = new JLabel("<html>Game Start!<br></html>");
        add(consoleOutput, BorderLayout.CENTER);

        JButton playButton = new JButton("Play Card");
        playButton.addActionListener(e -> {
            playCard(cardInputField.getText());
            updateUI();
        });

        inputPanel.add(playButton, BorderLayout.EAST);

        cardInputField = new JTextField();  // Add this line
        cardInputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = cardInputField.getText();

                // Check if the input is for drawing
                if (input.startsWith("d") && input.length() >= 2) {
                    drawCard();
                    updateUI();
                    cardInputField.setText(""); // Clear the input field
                    return;
                }

                // Check if the input is a valid card play
                if (input.length() >= 2) {
                    playCard(input);
                    updateUI();
                    cardInputField.setText(""); // Clear the input field
                } else {
                    // Inform the user that the input is invalid
                    consoleOutput.setText("<html>Invalid input. Please enter a valid card (e.g., '2C').<br></html>");
                }
            }
        });


        inputPanel.add(new JLabel("Enter Card:"), BorderLayout.WEST);
        inputPanel.add(cardInputField, BorderLayout.CENTER);

        // Move the addition of inputPanel here
        add(inputPanel, BorderLayout.NORTH);

        JButton drawButton = new JButton("Draw");
        drawButton.addActionListener(e -> {
            drawCard();
            updateUI();
        });

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));

        JButton startButton = new JButton("Start New Game");
        startButton.addActionListener(e -> {
            startNewGame();
            updateUI();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(drawButton);
        buttonPanel.add(exitButton);
        buttonPanel.add(startButton);

        add(buttonPanel, BorderLayout.SOUTH);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleCommand(e.getKeyChar());
            }
        });

        setFocusable(true);
        requestFocusInWindow();
        cardInputField.requestFocusInWindow();


        updateUI();
    }

    private void playCard(String card) {
        if (!roundOver && isValidPlay(card)) {
            center.add(card);
            switch (currentPlayerIndex) {
                case 1:
                    player1.remove(card);
                    break;
                case 2:
                    player2.remove(card);
                    break;
                case 3:
                    player3.remove(card);
                    break;
                case 4:
                    player4.remove(card);
                    break;
            }

            // Check if the round is over
            if (player1.isEmpty() || player2.isEmpty() || player3.isEmpty() || player4.isEmpty()) {
                endRound();
            } else {
                currentPlayerIndex = (currentPlayerIndex % 4) + 1;

                // Draw until a playable card is obtained for the next player
                drawUntilPlayable();
            }
        }
    }

    // Add a method to check if a player can play a card in the current round
    private boolean canPlayInRound() {
        return !getCurrentPlayerHand().isEmpty() || !deck.isEmpty();
    }

    private int calculateTrickScore() {
        int score = 0;
        for (String card : center) {
            char rank = card.charAt(1);
            switch (rank) {
                case 'A':
                    score += 11;
                    break;
                case 'K':
                case 'Q':
                case 'J':
                    score += 10;
                    break;
                case '1':
                    if (card.charAt(2) == '0') {
                        score += 10;
                    }
                    break;

                default:
                    // For other cards, you can implement your scoring logic
                    break;
            }
        }
        return score;
    }

    // Modify existing endRound method
    private void endRound() {
        // Display the score and end of round information
        int trickScore = calculateTrickScore();
        updatePlayerScores(trickScore);

        StringBuilder roundOutput = new StringBuilder("<html>");
        roundOutput.append("*** End of Round ***<br>");
        roundOutput.append("Score: Player1 = " + player1Score + " | Player2 = " + player2Score +
                " | Player3 = " + player3Score + " | Player4 = " + player4Score + "<br>");
        roundOutput.append("</html>");
        consoleOutput.setText(roundOutput.toString());

        // Check if the game is over
        if (checkGameOver()) {
            roundOver = true;
            displayGameOver();
        } else {
            // Start a new round
            startNewRound();
        }
    }

    // Add a method to check if the game is over based on empty hands
    private boolean checkGameOver() {
        return player1.isEmpty() || player2.isEmpty() || player3.isEmpty() || player4.isEmpty();
    }

    // Add a method to determine the winner based on the lowest score
    private int determineWinner() {
        int minScore = Math.min(player1Score, Math.min(player2Score, Math.min(player3Score, player4Score)));
        if (minScore == player1Score) return 1;
        if (minScore == player2Score) return 2;
        if (minScore == player3Score) return 3;
        return 4;
    }

    // Add a method to update player scores
    private void updatePlayerScores(int trickScore) {
        switch (currentPlayerIndex) {
            case 1:
                player1Score += trickScore;
                break;
            case 2:
                player2Score += trickScore;
                break;
            case 3:
                player3Score += trickScore;
                break;
            case 4:
                player4Score += trickScore;
                break;
        }
    }

    private void displayGameOver() {
        // Display the final game over information
        StringBuilder gameOverOutput = new StringBuilder("<html>");
        gameOverOutput.append("*** Game Over ***<br>");
        gameOverOutput.append("Winner: Player" + determineWinner() + "<br>");
        gameOverOutput.append("</html>");
        consoleOutput.setText(gameOverOutput.toString());
    }

    // Modify existing startNewRound method
    private void startNewRound() {
        // Reset relevant game state for a new round
        center.clear();
        currentPlayerIndex = 0;

        // Check if the game is over before starting a new round
        if (!roundOver) {
            determineFirstPlayer();
            shuffleAndDealCards();
        }

        roundOver = false; // Reset roundOver flag
        updateUI();
    }


    private void shuffleAndDealCards() {
        // Shuffle the deck and deal cards for a new round
        initializeDeck();
        dealCards();
    }

    // Add a method to handle drawing cards until a playable card is obtained
    // Modify existing drawUntilPlayable method
    // Modify the existing drawUntilPlayable method to handle the case when the player cannot play
    private void drawUntilPlayable() {
        while (!deck.isEmpty() && !canPlayInRound()) {
            drawCard();
        }
        updateUI();
    }


    // Add a method to check if the current player can play a card
    private boolean canPlayCard() {
        // Implement your logic to check if the current player has a playable card
        // You may want to check if any card in the player's hand can be played
        return playerHasPlayableCard();
    }

    // Add a method to check if any card in the player's hand can be played
    private boolean playerHasPlayableCard() {
        List<String> currentPlayerHand = getCurrentPlayerHand();
        String leadCard = center.get(0);
        for (String card : currentPlayerHand) {
            if (isValidPlay(card)) {
                return true;
            }
        }
        return false;
    }

    // Add a method to get the current player's hand
    private List<String> getCurrentPlayerHand() {
        switch (currentPlayerIndex) {
            case 1:
                return player1;
            case 2:
                return player2;
            case 3:
                return player3;
            case 4:
                return player4;
            default:
                return new ArrayList<>(); // Return an empty list if index is invalid
        }
    }

    private boolean isValidPlay(String card) {
        if (center.isEmpty()) {
            // Any card can be played if it's the first move
            return true;
        }

        // Implement your logic to check if the card is a valid play
        // You may want to check if it follows the suit or rank of the lead card
        String leadCard = center.get(0);
        char leadSuit = leadCard.charAt(0);
        char leadRank = leadCard.charAt(1);

        char playedSuit = card.charAt(0);
        char playedRank = card.charAt(1);

        // Special case for Ace being both high and low
        if (leadRank == 'A' && playedRank != '2') {
            return playedRank == 'K';
        }

        return (playedSuit == leadSuit) || (playedRank == leadRank);
    }



    private void handleCommand(char command) {
        switch (command) {
            case 's':
                startNewGame();
                updateUI();
                break;
            case 'x':
                System.exit(0);
                break;
            case 'd':
                drawUntilPlayable();
                drawCard();
                updateUI();
                break;
            default:
                // Check if the input is a card played by the current player
                if (Character.isLetter(command) && Character.isDigit(cardInputField.getText().charAt(1))) {
                    playCard("" + command);
                    updateUI();
                }
        }
    }


    private void startNewGame() {
        deck.clear();
        player1.clear();
        player2.clear();
        player3.clear();
        player4.clear();
        center.clear();
        currentPlayerIndex = 0;

        initializeDeck();
        dealCards();
        determineFirstPlayer();
    }


    private void determineFirstPlayer() {
        String leadCard = deck.remove(0);
        center.add(leadCard);

        switch (leadCard.charAt(1)) {
            case 'A':
            case '5':
            case '9':
            case 'K':
                currentPlayerIndex = 1;
                break;
            case '2':
            case '6':
            case '1':
                if (leadCard.charAt(2) == '0') {
                    currentPlayerIndex = 2;
                }
                break;

            case '3':
            case '7':
            case 'J':
                currentPlayerIndex = 3;
                break;
            case '4':
            case '8':
            case 'Q':
                currentPlayerIndex = 4;
                break;
        }

    }

    private void initializeDeck() {
        String[] suits = {"c", "d", "h", "s"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

        for (String suit : suits) {
            for (String rank : ranks) {
                deck.add(suit + rank);
            }
        }

        Collections.shuffle(deck);
    }

    private void dealCards() {
        for (int i = 0; i < 7; i++) {
            player1.add(deck.remove(0));
            player2.add(deck.remove(0));
            player3.add(deck.remove(0));
            player4.add(deck.remove(0));
        }
    }

    private void drawCard() {
        if (!deck.isEmpty()) {
            String card = deck.remove(0);
            switch (currentPlayerIndex) {
                case 1:
                    player1.add(card);
                    break;
                case 2:
                    player2.add(card);
                    break;
                case 3:
                    player3.add(card);
                    break;
                case 4:
                    player4.add(card);
                    break;
            }
            currentPlayerIndex = (currentPlayerIndex % 4) + 1;
        }
    }

    private void updateUI() {
        StringBuilder output = new StringBuilder("<html>");
        output.append("Trick #" + (center.size() / 4 + 1) + "<br>");
        output.append("Player1: " + player1 + " (Score: " + player1Score + ")<br>");
        output.append("Player2: " + player2 + " (Score: " + player2Score + ")<br>");
        output.append("Player3: " + player3 + " (Score: " + player3Score + ")<br>");
        output.append("Player4: " + player4 + " (Score: " + player4Score + ")<br>");
        output.append("Center : " + center + "<br>");
        output.append("Deck : " + deck + "<br>");
        output.append("Score: Player1 = " + player1Score + " | Player2 = " + player2Score +
                " | Player3 = " + player3Score + " | Player4 = " + player4Score + "<br>");
        output.append("Turn : Player" + currentPlayerIndex + "<br>");
        cardInputField.setText("");
        output.append("</html>");
        consoleOutput.setText(output.toString());
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GoBoomGame().setVisible(true));
    }
}
