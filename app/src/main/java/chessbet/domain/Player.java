package chessbet.domain;

import com.chess.engine.Alliance;

public enum Player {
    BLACK {
        @Override
        public Alliance getAlliance() {
            return Alliance.BLACK;
        }
    },
    WHITE{
        @Override
        public Alliance getAlliance() {
            return Alliance.WHITE;
        }
    };

    public abstract Alliance getAlliance();
}
