package com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.network;

import com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.AppConfig;
import com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.restbean.Author;
import com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.restbean.ModifiedTodoPayload;
import com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.restbean.ToDoItem;
import com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.restbean.TokenID;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by rogerh on 8/2/2018.
 */

public class APIServiceProvider {

    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;
    private static final HttpLoggingInterceptor.Level LOGGING_LEVEL = HttpLoggingInterceptor.Level.BODY;

    private HttpLoggingInterceptor loggingInterceptor;
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private APIInterface apiInterface;

    private static APIServiceProvider apiServiceProvider;

    private  APIServiceProvider(){

        loggingInterceptor = new HttpLoggingInterceptor().setLevel(LOGGING_LEVEL);
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .addNetworkInterceptor(loggingInterceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(ToDoRestAPIs.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        apiInterface = retrofit.create(APIInterface.class);
    }


    public static APIServiceProvider getAPIServiceProvider(){
        if(apiServiceProvider == null){
            apiServiceProvider = new APIServiceProvider();
        }
        return apiServiceProvider;
    }

    public Call<Author> registerAuthor(Author author){
        return apiInterface.registerAuthor(author);
    }

    public Call<TokenID> loginAuthor(Author author){
        return apiInterface.loginAuthor(author);
    }

    public Call<List<ToDoItem>> getToDoList(String authorMailId){
        return apiInterface.getToDoList(authorMailId, AppConfig.getSavedSessionTokenID());
    }

    public Call<ToDoItem> modifyToDoItem(ModifiedTodoPayload modifiedTodoPayload){
        return apiInterface.modifyToDoItem(AppConfig.getSavedSessionTokenID(), modifiedTodoPayload);
    }

    public Call<ResponseBody> deleteToDoItem(ToDoItem toDoItem){
        return apiInterface.deleteToDoItem(AppConfig.getSavedSessionTokenID(), toDoItem);
    }

    public Call<ToDoItem> addNewToDoItem(ToDoItem toDoItem){
        return apiInterface.addNewToDoItem(AppConfig.getSavedSessionTokenID(), toDoItem);
    }

}
