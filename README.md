[![Build Status](https://travis-ci.com/Magz8984/chess-bet.svg?token=1t1EwrDpq3sLA8yRH7Ea&branch=test_env)](https://travis-ci.com/Magz8984/chess-bet)

## Test Environment

## Releases 

### v1.0-beta (Chess MVP)


#### Features

- Stockfish 10 engine support for game hinting
-  Board View by Chess MVP
- Chess Engine by Chess MVP for game logic
- Move Undo Redo
- Color Picker (Allows users to change board colors to any color combination they want).
- Flip board functionality in custom board
- Game timer in on line play.
- Possible quiet moves and attack moves highlight
- Game state display views.
- Puzzle creation v1 by users.
- Challenge creation on line play if no challenge was found in the process
- Challenge acceptance based on metrics such as the game duration min and max elo expected, time etc.
- Profile image change functionality.
- User profile  management (change password, change user name and change email)
- Play AI engine at depth 2 plays at an average rating of 850 (Not bad). Depth 2 is less costly for the algorithm since it evaluates millions of positions while using the standard board evaluator for output
- Game storage locally on .pgn files.
- Game sorting on time.
- Landscape and Portrait game support. Game states are not lost on orientation change.
- Taken Pieces Panel
- Move log display for both users.
- Best move and ponder move by stockfish 10.
- Image caching by glide
- HTTP post requests to cloud functions by Okhttp
- On line Game forfeit functionality
- ECO moves support on game play.
- Terms of service acceptance fragment.
- Custom network state functionality implementation
- Send email verification message on sign up.
- Forgot password feature.
- Play versus online users

### v1.1-beta (Chess MVP)

#### Features

- Stockfish 11 engine support for game hinting and analysis

### Improvement
- Fix challenge creation
- Fix initial board setup
- Push notifications for friendly challenge (FCM)
- Fix games adapter number format error
- Replace match evaluation trigger with a http function call

### v1.1.3-beta (Chess MVP)

### Improvements

- Android -21 connectivity library incompatibility 
- Fix ECOBuilder end of file error
- Add xhdpi screen support
- Fix challenge messages.
- Enable one challenge notification on app start up.

### v1.1.4-beta (Chess MVP)

#### Features

- PGN files can be viewed from the app

### Improvements
- Fix challenge creation process when user is requested for a challenge

### v1.1.5-beta (Chess MVP)

### Improvements

- Fix Play Online Bugs

### 1.1.6-beta (Chess MVP)

#### Improvements

- Add computer skill levels when playing versus computer

### 1.1.7-beta (Chess-MVP)

#### Improvements

- Fix stockfish depth on different skill levels
- Fix UI not being updated after a certain move
- Fix hints from stockfish not showing up for certain positions

### 1.1.8-beta (Chess MVP)

#### Improvements

- Fix bitmap OOM error
- Replace Play Computer Settings Dialog Fragment with Activity

#### 1.1.9-beta(Chess MVP)
- Fix skill level intent data

#### 1.2.1-beta(Chess MVP)
- Fix error when getting no move from stockfish

#### 1.2.5 release
- Retain v1.0 app icon
- Increase level grid view height
- Create name profile fragment
- Fix timer lapsed logic