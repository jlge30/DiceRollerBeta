package com.jose.diceroller.db;

import com.jose.diceroller.PlayersOnlineActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiService {

    @GET(Bote.TABLE_BOTE +".json")
    Call<Bote> obtenerBoteMonedas();
    @PUT("boteMonedas.json")
    Call<Void> ajustarValorDelBote(@Body int nuevoValor);

    @GET("boteMonedas.json")
    Call<Integer> obtenerValorBote();

    @GET(Bote.TABLE_BOTE+".json")
    void obtenerBoteMonedasCallback(Callback<Bote> callback);

    @GET(PlayerHistory.TABLE_JUGADORES+".json")
    Call<List<PlayerHistory>> obtenerJugadores();

    @POST(PlayerHistory.TABLE_JUGADORES+".json")
    Call<PlayerHistory> agregarJugador(@Body PlayerHistory jugador);
}
