package com.supertictactoe.common;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

import net.thegreshams.firebase4j.model.FirebaseResponse;
import net.thegreshams.firebase4j.service.Firebase;
import net.thegreshams.firebase4j.error.FirebaseException;
import net.thegreshams.firebase4j.error.JacksonUtilityException;

import com.supertictactoe.components.*;
import com.supertictactoe.components.Contender.Side;

import static com.supertictactoe.common.SuperTicTacToeStrings.*;

public class StoredProcedure {

    private static final Logger _logger =
        Logger.getLogger(StoredProcedure.class.getName());

    private enum ParseState {kNull, kMoveParse, kStateParse};

    /* This guy is ugly and unruly, but a necessary evil, and the
     * critial path as well */
    public static Game SPParseFirebase (Firebase fb)
        throws FirebaseException {

        FirebaseResponse firebaseResponse = fb.get();

        if (firebaseResponse.getBody() == null) {
            throw new FirebaseException("Null Data Returned: " + firebaseResponse.getRawBody());
        }

        /* Init game baord */
        Game gm = new Game(kNumGameBoards);
        TreeMap<Date, Move> move_map = new TreeMap<Date, Move>();

        ParseState state = ParseState.kNull;
        int states_completed = 0;

        /* Start parsing the firebase response body */
        Set<Entry<String, Object>> firebaseResponseBody
            = firebaseResponse.getBody().entrySet();

        Iterator<Entry<String, Object>> interMoveIter
            = firebaseResponse.getBody().entrySet().iterator();


        while (interMoveIter.hasNext()) {
            Entry<String, Object> interMovePair = interMoveIter.next();

            /* System.out.println("inter: " + interMovePair.getKey()); */

            /* TODO: REMOVE DUPLICATED CODE */
            if (interMovePair.getKey().equals(kGameRefSubkeyMoves)) {
                if (((states_completed >> ParseState.kMoveParse.ordinal()) & 0x01) == 0) {
                    state = ParseState.kMoveParse;
                    states_completed |= (0x01 << ParseState.kMoveParse.ordinal());
                }
            } else if (interMovePair.getKey().equals(kGameRefSubkeyState)) {
                if (((states_completed >> ParseState.kStateParse.ordinal()) & 0x01) == 0) {
                    state = ParseState.kStateParse;
                    states_completed |= (0x01 << ParseState.kStateParse.ordinal());
                }
            }
            /* END TODO */

            if (interMovePair.getValue() instanceof String) {
                /* System.out.println("string: " + interMovePair.getValue()); */
            } else {
                Iterator<Entry<String, Object>> intraMoveIter
                    = ((Map<String, Object>) interMovePair.getValue()).entrySet().iterator();
                while(intraMoveIter.hasNext()) {
                    Entry<String, Object> intraMovePair = intraMoveIter.next();
                    /* System.out.println("pair:" + intraMovePair.getKey() + " = " + intraMovePair.getValue()); */

                    /* Add each move */
                    if (state == ParseState.kMoveParse &&
                        (intraMovePair.getValue() instanceof LinkedHashMap)) {

                        Iterator<Entry<String, Object>> moveIter
                            = ((Map<String, Object>) intraMovePair.getValue()).entrySet().iterator();
                        /* System.out.println("INSTANCE: " + intraMovePair.getValue().getClass()); */
                        int id_inner = -1;
                        int id_outer = -1;
                        String player = "";
                        DateFormat df = new SimpleDateFormat(kTimestampFormat);
                        Date timestamp = null;

                        while (moveIter.hasNext()) {
                            Entry<String, Object> movePair = moveIter.next();
                            if (movePair.getKey().equals(kGameMovesIDOuter)) {
                                id_outer = (Integer) movePair.getValue();
                            } else if (movePair.getKey().equals(kGameMovesIDInner)) {
                                id_inner = (Integer) movePair.getValue();
                            } else if (movePair.getKey().equals(kGameMovesPlayer)) {
                                player = (String) movePair.getValue();
                            } else if (movePair.getKey().equals(kGameMovesTimestamp)) {
                                try {
                                    timestamp = df.parse((String) movePair.getValue());
                                } catch (ParseException e) {
                                    /* This is not an exceptional condition, why make a try/catch?
                                     * Java is weird. */
                                }
                            }
                        }

                        /* We can add a properly parsed move */
                        Contender contender = SPBuildContender(player);
                        if (id_inner > -1 && id_outer > -1 &&
                            contender != null && timestamp != null) {

                            Move move = new Move(id_outer, id_inner, contender.getTeam());
                            move_map.put(timestamp, move);
                        }

                        /* System.out.println("PLAYER: " + player + ", ID_OUTER: " + id_outer + ", ID_INNER: " + id_inner); */
                    } else if (state == ParseState.kStateParse) {

                        /* Do I actually need to read in state here? I
                         * only need to write it... */
                    }
                }
            }
        }

        /* Why is iterating through a map in java so stupidly verbose? */
        Iterator<Entry<Date, Move>> it = move_map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Date, Move> pairs = it.next();
            Move current_move = (Move) pairs.getValue();
            if (gm.play(current_move)) {
                System.out.println("PLAYED: " + current_move);
            }
            /* Save a bit of memory */
            it.remove();
        }

        return gm;
    }

    public static Contender SPBuildContender(String contender) {

        if (contender.equals(kGamePlayerX)) {
            return new Contender(Side.X);
        } else if (contender.equals(kGamePlayerO)) {
            return new Contender(Side.O);
        }

        return null;
    }

    public static String SPStringifySide(Side s) {

        if (s == Side.X) {
            return kGamePlayerX;
        } else if (s == Side.O) {
            return kGamePlayerO;
        }

        return null;
    }

    public static Side SPGetOpposingSide(Side s) {

        if (s == Side.X) {
            return Side.O;
        } else if (s == Side.O) {
            return Side.X;
        }

        return null;
    }

    public static boolean SPPushMove(Firebase fb, Move move) {

        DateFormat df = new SimpleDateFormat(kTimestampFormat);
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        String player = SPStringifySide(move.getSide());

        if (player != null) { data.put(kGameMovesPlayer, player); }
        else { return false; }

        data.put(kGameMovesIDOuter, move.getBoard());
        data.put(kGameMovesIDInner, move.getCell());
        data.put(kGameMovesTimestamp, df.format(new Date()));

        FirebaseResponse resp = null;
        try {
            resp = fb.post("moves", data);
        } catch (JacksonUtilityException e) {
            e.printStackTrace();
        } catch (FirebaseException e) {
            e.printStackTrace();
        }

        return resp==null ? resp.getSuccess() : false;
    }

    public static boolean SPUpdateState(Firebase fb, Game game, Move move) {

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        String player = SPStringifySide(SPGetOpposingSide(move.getSide()));

        if (player != null) { data.put(kGameStateCurrentPlayer, player); }
        else { return false; }

        Map<String, Object> boards_won = new LinkedHashMap<String, Object>();
        for (int i=0; i<game.boards.size(); ++i) {
            if (game.boards.get(i).isWon()) {
                Side winner = game.boards.get(i).getWinner();
                if (winner != null) {
                    boards_won.put(""+i, SPStringifySide(winner));
                }
            }
        }

        Map<String, Object> boards_avail = new LinkedHashMap<String, Object>();
        int j=0;
        for (Integer i : game.validBoards()) {
            boards_avail.put(""+j++, ""+i);
            System.out.println("AVAIL BOARD: " + i);
        }
        
        String game_winner = SPStringifySide(game.getWinner());
        
        data.put(kGameStateBoardsWon, boards_won);
        data.put(kGameStateBoardsAvail, boards_avail);

        if (game.isWon()) {
            data.put(kGameStateWinner, game_winner);
            data.put(kGameStateWon, "1");
        }

        FirebaseResponse resp = null;
        try {
            resp = fb.put("state", data);
        } catch (JacksonUtilityException e) {
            e.printStackTrace();
        } catch (FirebaseException e) {
            e.printStackTrace();
        }

        return resp==null ? resp.getSuccess() : false;
    }
}
