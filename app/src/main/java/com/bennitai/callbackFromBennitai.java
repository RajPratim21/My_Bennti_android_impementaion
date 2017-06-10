package com.bennitai;

import org.json.JSONArray;

/**
 * Created by Benjamin on 2/22/2017.
 */
public interface callbackFromBennitai
{
    public String gotReplyFromBennitAi(String BennitReply, Boolean isSuggestion, JSONArray SuggestionArr);

}
