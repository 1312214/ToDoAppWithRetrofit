package com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.network;

import com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.restbean.Author;
import com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.restbean.ModifiedTodoPayload;
import com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.restbean.ToDoItem;
import com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.restbean.TokenID;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by rogerh on 8/2/2018.
 */

public interface APIInterface {

    @POST(ToDoRestAPIs.registerAuthor)
    Call<Author> registerAuthor(@Body Author author);

    @POST(ToDoRestAPIs.login)
    Call<TokenID> loginAuthor(@Body Author author);

    @GET(ToDoRestAPIs.getToDoList + "{authorMailId}")
    Call<List<ToDoItem>> getToDoList(@Path(value = "authorMailId") String authorMailId, @Header(value = "token") String token);

    @POST(ToDoRestAPIs.addToDo)
    Call<ToDoItem> addNewToDoItem(@Header(value = "token") String token, @Body ToDoItem toDoItem);

    @PUT(ToDoRestAPIs.modifyToDo)
    Call<ToDoItem> modifyToDoItem(@Header(value = "token") String token, @Body ModifiedTodoPayload modifiedTodoPayload);

    @HTTP(method = "DELETE", path = ToDoRestAPIs.deleteToDo, hasBody = true)
    Call<ResponseBody> deleteToDoItem(@Header(value = "token") String token, @Body ToDoItem toDoItem);

}
