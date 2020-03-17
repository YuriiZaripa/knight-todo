package com.knighttodo.knighttodo;

import static com.knighttodo.knighttodo.Constants.API_BASE_BLOCKS;
import static com.knighttodo.knighttodo.Constants.API_BASE_TODOS;
import static com.knighttodo.knighttodo.Constants.API_BASE_URL;
import static com.knighttodo.knighttodo.Constants.API_GET_TODOS_BY_BLOCK_ID;
import static com.knighttodo.knighttodo.Constants.BASE_EXPERIENCE_URL;
import static com.knighttodo.knighttodo.Constants.BASE_READY;

import com.knighttodo.knighttodo.gateway.privatedb.representation.Todo;
import com.knighttodo.knighttodo.gateway.privatedb.representation.TodoBlock;

public class TestConstants {

    public static final String JSON_ROOT = "$.";
    public static final String PARAMETER_FALSE = "false";
    public static final String PARAMETER_TRUE = "true";

    public static String buildGetBlockByIdUrl(String id) {
        return API_BASE_BLOCKS + "/" + id;
    }

    public static String buildDeleteBlockByIdUrl(String id) {
        return API_BASE_BLOCKS + "/" + id;
    }

    public static String buildGetTodoByIdUrl(String blockId, String id) {
        return API_BASE_BLOCKS + "/" + blockId + API_BASE_TODOS + "/" + id;
    }

    public static String buildDeleteTodoByIdUrl(String blockId, String id) {
        return API_BASE_BLOCKS + "/" + blockId + API_BASE_TODOS + "/" + id;
    }

    public static String buildGetTodosByBlockIdUrl(String blockId, String id) {
        return API_BASE_BLOCKS + "/" + blockId + API_BASE_TODOS + API_GET_TODOS_BY_BLOCK_ID + "/" + id;
    }

    public static String buildUpdateTodoReadyBaseUrl(TodoBlock todoBlock, Todo todo) {
        return API_BASE_BLOCKS + "/" + todoBlock.getId() + API_BASE_TODOS + "/" + todo.getId() + BASE_READY;
    }

    public static String buildJsonPathToId() {
        return JSON_ROOT + "id";
    }

    public static String buildJsonPathToLength() {
        return JSON_ROOT + "length()";
    }

    public static String buildJsonPathToTodoName() {
        return JSON_ROOT + "todoName";
    }

    public static String buildJsonPathToScaryness() {
        return JSON_ROOT + "scaryness";
    }

    public static String buildJsonPathToHardness() {
        return JSON_ROOT + "hardness";
    }

    public static String buildJsonPathToTodoBlockId() {
        return JSON_ROOT + "todoBlockId";
    }

    public static String buildJsonPathToBlockName() {
        return JSON_ROOT + "blockName";
    }

    public static String buildJsonPathToTodosLength() {
        return JSON_ROOT + "todos.length()";
    }
}
