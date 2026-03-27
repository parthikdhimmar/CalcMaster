package com.example.calcmaster;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CurrencyApiService {
    @GET("{currency}?api_key=ae1fa5a80db19270bd942c6e")
    Call<ExchangeRates> getExchangeRates(@Path("currency") String currency);
}
