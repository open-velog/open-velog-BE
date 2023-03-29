package com.openvelog.openvelogbe.boardSearch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NByteArrayEntity;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.action.index.IndexRequest;
import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.entity.BoardDocument;
import com.openvelog.openvelogbe.common.repository.BoardRepository;
import org.opensearch.client.Request;
import org.opensearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardSearchService {

    private final BoardRepository boardRepository;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final int batchSize = 10000;
    private Long lastProcessedBoardId = 0L;
    @Value("${DATABASE_URL}")
    private String dbUrl;

    @Value("${DATABASE_USERNAME}")
    private String dbUsername;

    @Value("${DATABASE_PASSWORD}")
    private String dbPassword;

    @PostConstruct
    private void initializeLastProcessedBoardId() {
        // 저장된 lastProcessedBoardId 값을 가져옵니다.
        // 이 값은 이전 호출에서 처리한 마지막 Board의 id입니다.
        lastProcessedBoardId = retrieveLastProcessedBoardIdFromDatabase();
    }

    public void indexAllBoards() throws IOException {
        int offset = 0;
        while (true) {
            // lastProcessedBoardId 이후의 batchSize개의 Board 데이터를 가져옵니다.
            List<Board> boards = boardRepository.findByIdGreaterThanOrderByIdAsc(lastProcessedBoardId, PageRequest.of(offset, batchSize)).getContent();
            if (boards.isEmpty()) {
                break;
            }
            HttpEntity entity = createBulkRequestEntity(boards);
            Request request = new Request("POST", "/board/_bulk");
            request.setEntity(entity);
            restClient.performRequest(request);

            // 마지막으로 처리한 Board의 id를 업데이트합니다.
            lastProcessedBoardId = boards.get(boards.size() - 1).getId();
            updateLastProcessedBoardIdInDatabase(lastProcessedBoardId);

            offset += batchSize;

        }
    }

    public void updateBoard(Board board) throws IOException {
        String endpoint = "/board/_update/" + board.getId();

        ObjectNode docNode = objectMapper.createObjectNode();
        docNode.put("title", board.getTitle());
        docNode.put("content", board.getContent());
        docNode.put("modified_at", board.getModifiedAt().format(DateTimeFormatter.ISO_DATE_TIME));
        docNode.put("view_count", board.getViewCount());

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.set("doc", docNode);

        HttpEntity entity = new NStringEntity(requestBody.toString(), ContentType.APPLICATION_JSON);
        Request request = new Request("POST", endpoint);
        request.setEntity(entity);
        restClient.performRequest(request);
    }

    private HttpEntity createBulkRequestEntity(List<Board> boards) throws JsonProcessingException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (Board board : boards) {
            BoardDocument boardDocument = BoardDocument.create(board);
            IndexRequest indexRequest = new IndexRequest("board");
            indexRequest.id(boardDocument.getId().toString());

            // Create JSON object using Jackson ObjectMapper
            ObjectNode indexNode = objectMapper.createObjectNode();
            indexNode.putObject("index")
                    .put("_index", indexRequest.index())
                    .put("_id", indexRequest.id());
            out.write(objectMapper.writeValueAsBytes(indexNode));
            out.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));

            // Create JSON object using Jackson ObjectMapper
            ObjectNode boardNode = objectMapper.valueToTree(boardDocument);
            out.write(objectMapper.writeValueAsBytes(boardNode));
            out.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
        }
        return new NByteArrayEntity(out.toByteArray(), ContentType.APPLICATION_JSON);
    }

    private Connection getConnection() throws SQLException {
        String url = dbUrl;
        String user = dbUsername;
        String password = dbPassword;
        return DriverManager.getConnection(url, user, password);
    }

    private Long retrieveLastProcessedBoardIdFromDatabase() {
        Long lastProcessedBoardId = 0L;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "SELECT last_processed_board_id FROM boards";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                lastProcessedBoardId = rs.getLong("last_processed_board_id");
            }
        } catch (SQLException ex) {
            // handle exception
        }
        return lastProcessedBoardId;
    }

    private void updateLastProcessedBoardIdInDatabase(Long lastProcessedBoardId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE some_table SET last_processed_board_id = ?")) {
            ps.setLong(1, lastProcessedBoardId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            // handle exception
        }
    }
}
