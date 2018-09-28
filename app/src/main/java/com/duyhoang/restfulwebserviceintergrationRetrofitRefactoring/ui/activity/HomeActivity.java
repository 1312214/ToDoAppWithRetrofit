package com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.AppConfig;
import com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.R;
import com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.network.APIServiceProvider;
import com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.network.Util;
import com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.restbean.ModifiedTodoPayload;
import com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.restbean.ToDoItem;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = HomeActivity.class.getSimpleName();

    EditText etToDo, etPlace, etToDoID, etModifiedToDoContent;
    Button btnAdd, btnDelete, btnUpdate;
    TextView txtToDoList;
    ScrollView svParent, svToDoList;


    List<ToDoItem> todoList;
    ToDoItem itemToBeDeleted;
    ToDoItem proposedTodo;
    ModifiedTodoPayload payload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(todoList == null){
            todoList = new ArrayList<ToDoItem>();
        }
        initUI();
        getToDoList();
    }

    private void initUI() {

        getSupportActionBar().setIcon(R.drawable.ic_event_note_white_24dp);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etToDo = findViewById(R.id.edit_home_activity_todocontent);
        etPlace = findViewById(R.id.edit_home_activity_place);
        btnAdd = findViewById(R.id.button_home_activity_add);
        txtToDoList = findViewById(R.id.text_home_activity_todolist);
        etToDoID = findViewById(R.id.edit_home_activity_idtodo_modify);
        btnDelete = findViewById(R.id.button_home_activity_delete);
        etModifiedToDoContent = findViewById(R.id.edit_home_activity_modified_todocontent);
        btnUpdate = findViewById(R.id.button_home_activity_update);
        svParent = findViewById(R.id.scrollView_parent);
        svToDoList = findViewById(R.id.scrollView_children);

        btnAdd.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        svParent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                findViewById(R.id.scrollView_children).getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        svToDoList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_home_activity_add: addNewToDo();
                break;
            case R.id.button_home_activity_update: updateToDo();
                break;
            case R.id.button_home_activity_delete: deleteToDoById();
                break;
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.option_logout: logout();
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }

    }

    private void logout() {
        startActivity(new Intent(this, LoginActivity.class));
        AppConfig.clearSharedPreference();
    }






    private void getToDoList(){
        if(Util.isAppOnline(AppConfig.getContext())){
            showBusyDialog("Getting ToDoList");
            Call<List<ToDoItem>> getToDoListCall = APIServiceProvider.getAPIServiceProvider()
                    .getToDoList(AppConfig.getRegisteredSuccessfulAuthor().getAuthorEmailId());
            getToDoListCall.enqueue(new Callback<List<ToDoItem>>() {
                @Override
                public void onResponse(Call<List<ToDoItem>> call, Response<List<ToDoItem>> response) {
                    dismissBusyDialog();
                    if(response.code() != 404) {
                        setToDoList(response.body());
                    } else {
                        toastMessage("You have nothing to do", Toast.LENGTH_SHORT);
                    }

                }

                @Override
                public void onFailure(Call<List<ToDoItem>> call, Throwable t) {
                    dismissBusyDialog();
                    toastMessage(t.getMessage(), Toast.LENGTH_SHORT);
                }
            });
        }
        else {
            toastMessage("Network issue occured", Toast.LENGTH_SHORT);
        }

    }


    private void addNewToDo() {
        String todoContent = etToDo.getText().toString();
        String place = etPlace.getText().toString();
        String authorMailID = AppConfig.getRegisteredSuccessfulAuthor().getAuthorEmailId();
        ToDoItem todo = new ToDoItem(0, todoContent, place, authorMailID);

        if(Util.isAppOnline(AppConfig.getContext())) {
            showBusyDialog("Adding");
            Call<ToDoItem> addingItemCall = APIServiceProvider.getAPIServiceProvider().addNewToDoItem(todo);
            addingItemCall.enqueue(new Callback<ToDoItem>() {
                @Override
                public void onResponse(Call<ToDoItem> call, Response<ToDoItem> response) {
                    dismissBusyDialog();
                    addNewToDoItem2ToDoList(response.body());
                }

                @Override
                public void onFailure(Call<ToDoItem> call, Throwable t) {
                    dismissBusyDialog();
                    toastMessage(t.getMessage(), Toast.LENGTH_SHORT);
                }
            });
        } else {
            toastMessage("Network issue", Toast.LENGTH_SHORT);
        }



    }


    private void updateToDo() {
        String IdTodoModified = etToDoID.getText().toString();
        String modifiedContent = etModifiedToDoContent.getText().toString();

        ToDoItem currentToDo = getToDoById(Integer.valueOf(IdTodoModified));
        if(currentToDo != null){
            if(Util.isAppOnline(AppConfig.getContext())){
                showBusyDialog("Updating");
                proposedTodo = new ToDoItem(currentToDo.getId(), modifiedContent, currentToDo.getPlace(), currentToDo.getAuthorMail());
                payload = new ModifiedTodoPayload(currentToDo, proposedTodo);

                Call<ToDoItem> modifyToDoCall = APIServiceProvider.getAPIServiceProvider().modifyToDoItem(payload);
                modifyToDoCall.enqueue(new Callback<ToDoItem>() {
                    @Override
                    public void onResponse(Call<ToDoItem> call, Response<ToDoItem> response) {
                        dismissBusyDialog();
                        updateModifiedToDoItem(response.body());
                        updateToDoList();
                        clearAllTextInput();
                    }

                    @Override
                    public void onFailure(Call<ToDoItem> call, Throwable t) {
                        dismissBusyDialog();
                        toastMessage("Fail: " + t.getMessage(), Toast.LENGTH_SHORT);
                    }
                });
            }
            else {
                toastMessage("Network issue", Toast.LENGTH_SHORT);
            }
        }
        else {
            toastMessage("entered ToDo ID is not exist", Toast.LENGTH_SHORT);
        }


    }

    private void deleteToDoById(){
        String todoId = etToDoID.getText().toString();
        itemToBeDeleted = getToDoById(Integer.valueOf(todoId));
        if(itemToBeDeleted != null){
            if(Util.isAppOnline(AppConfig.getContext())){
                showBusyDialog("Deleting");
                Call<ResponseBody> deteletItemCall = APIServiceProvider.getAPIServiceProvider().deleteToDoItem(itemToBeDeleted);
                deteletItemCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dismissBusyDialog();
                        if(response.code() == 204){
                            toastMessage("Deleted", Toast.LENGTH_SHORT);
                            removeToDoItemFromToDoList(itemToBeDeleted.getId());
                        } else {
                            toastMessage("Error: " + response.message(), Toast.LENGTH_LONG);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        dismissBusyDialog();
                        toastMessage("Failed: " + t.getMessage(), Toast.LENGTH_SHORT);
                    }
                });
            }
            else{
                toastMessage("Network issue", Toast.LENGTH_SHORT);
            }
        }
        else {
            toastMessage("entered ToDo ID is not exist", Toast.LENGTH_SHORT);
        }
    }



    private void updateModifiedToDoItem(ToDoItem todo) {
        for(int i = 0; i < todoList.size(); i++){
            if(todoList.get(i).getId() == todo.getId()){
                todoList.set(i, todo);
                break;
            }
        }
    }

    private void removeToDoItemFromToDoList(int id) {

        for(int i = 0; i < todoList.size(); i++){
            if(todoList.get(i).getId() == id){
                todoList.remove(i);
                break;
            }
        }
        updateToDoList();
        clearAllTextInput();
    }

    private void updateToDoList() {
        txtToDoList.setText("");
        for(ToDoItem item : todoList){
            String line = item.getId() + " - " +item.getTodoString() + ", " + item.getPlace() + "\n\n";
            txtToDoList.append(line);
            scrollDownToDoList();
        }
    }

    public void setToDoList(final List<ToDoItem> list) {
        for(ToDoItem item : list) {
            todoList.add(item);
            String line = item.getId() + " - " + item.getTodoString() + ", " + item.getPlace() + "\n\n";
            txtToDoList.append(line);
        }
    }

    private void addNewToDoItem2ToDoList(ToDoItem toDo) {
        todoList.add(toDo);
        String line = toDo.getId() + " - " + toDo.getTodoString() + ", " + toDo.getPlace() + "\n";
        txtToDoList.append(line);
        scrollDownToDoList();
        clearAllTextInput();
    }

    private void clearAllTextInput() {
        etToDo.setText("");
        etPlace.setText("");
        etModifiedToDoContent.setText("");
        etToDoID.setText("");
    }

    private void scrollDownToDoList() {
        svToDoList.fullScroll(View.FOCUS_DOWN);
    }




    private ToDoItem getToDoById(int id){
        for(ToDoItem item : todoList){
            if(item.getId() == id){
                return item;
            }
        }
        return null;
    }
}
