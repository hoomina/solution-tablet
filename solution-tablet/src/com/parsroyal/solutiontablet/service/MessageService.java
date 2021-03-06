package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.model.CreateMessageRequest;
import com.parsroyal.solutiontablet.data.model.Message;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;


public interface MessageService {

  @GET("notification/all")
  Call<List<Message>> getAllMessages();

  @POST("notification/create")
  Call<Message> sendMessage(@Body CreateMessageRequest createMessageRequest);
}
