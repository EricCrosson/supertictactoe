package com.supertictactoe.supertictactoe;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.thegreshams.firebase4j.error.FirebaseException;
import net.thegreshams.firebase4j.service.Firebase;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.supertictactoe.supertictactoe.components.*;
import com.supertictactoe.supertictactoe.components.StrategyFactory.StrategyType;

import static com.supertictactoe.Common.StoredProcedure.*;

public class AIMoveServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {

        /* STUB */
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

        final String game_url = request.getParameter("game_url");
        final String player_just_played = request.getParameter("player_just_played");
        final String ai_difficulty_str = request.getParameter("ai_difficulty");

        JSONObject json = new JSONObject();

        if ((game_url == null) || (player_just_played == null) ||
            (ai_difficulty_str == null)) {
            try {
                /* Stupid java */
                json.append("success", "false");
                json.append("reson", "null_data");
                json.write(response.getWriter());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return;
        }

        int ai_difficulty = Integer.parseInt(ai_difficulty_str);

        /* create the Firebase reference */
        try {
            Firebase firebase = new Firebase(game_url);
            
            /* TODO: CHANGE THIS TO PERFECT_WITH_THRESHOLD */
            Bot bot = new Bot(SPGetOpposingSide(SPBuildContender(player_just_played).getTeam()),
                              StrategyType.RANDOM);
            /* END TODO */

            Game gm = SPParseFirebase(firebase);
            Move move = bot.nextMove(gm);
            
            if (gm.play(move)) {
                System.out.println("AI VALID MOVE PLAYED!");
                SPPushMove(firebase, move);
                SPUpdateState(firebase, gm, move);
                json.append("success", "true");
            } else {
                System.out.println("AI INVALID MOVE IGNORED!");
                json.append("success", "false");
                json.append("reason", "ai_invalid_move");
            }
            json.write(response.getWriter());
        } catch (FirebaseException e) {
            /* STUB */
            e.printStackTrace();
            try {
                /* Stupid java */
                json.append("success", "false");
                json.append("reason", "ai_firebase_exception");
                json.write(response.getWriter());
            } catch (JSONException jse) {
                // TODO Auto-generated catch block
                jse.printStackTrace();
            }
        } catch (JSONException e) {
            // This is stupid; adding to a JSON object is NOT exceptional!
            // TODO Auto-generated catch block
            e.printStackTrace(); 
            try {
                /* Stupid java */
                json.append("success", "false");
                json.append("reason", "ai_json_exception");
                json.write(response.getWriter());
            } catch (JSONException jse) {
                // TODO Auto-generated catch block
                jse.printStackTrace();
            }
        }
    }
}