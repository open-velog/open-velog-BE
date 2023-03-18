package com.openvelog.openvelogbe.dummy;

import com.openvelog.openvelogbe.common.entity.Blog;
import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.repository.BlogRepository;
import com.openvelog.openvelogbe.common.repository.BoardRepository;
import com.openvelog.openvelogbe.crawling.entity.CrawledBoard;
import com.openvelog.openvelogbe.crawling.repository.CrawledBoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Lazy
@Slf4j
public class BoardDummyGenerator extends DummyGenerator<Board, BoardRepository> {

    private int BATCH_SIZE = 1000;

    private BlogRepository blogRepository;

    private CrawledBoardRepository crawledBoardRepository;

    private List<Blog> blogs;

    private List<CrawledBoard> crawledBoards;

    @Autowired
    BoardDummyGenerator(
            BoardRepository repository,
            CrawledBoardRepository crawledBoardRepository,
            BlogRepository blogRepository
    ) {
        super(repository);
        this.crawledBoardRepository = crawledBoardRepository;
        this.blogRepository = blogRepository;
        this.crawledBoards = crawledBoardRepository.findAll(PageRequest.of(0, BATCH_SIZE, Sort.unsorted())).getContent();
        this.blogs = blogRepository.findAll();
    }

    @Override
    public Board generateDummyEntityOfThis() {
        Blog randomlySelectedBlog = this.blogs.get(random.nextInt(blogs.size()));
        CrawledBoard randomlySelectedCrawledBoard = this.crawledBoards.get(random.nextInt(crawledBoards.size()));

        Board dummyBoard = Board.builder()
                .title(randomlySelectedCrawledBoard.getTitle())
                .content(randomlySelectedCrawledBoard.getContent())
                .blog(randomlySelectedBlog)
                .viewCount(0L)
                .build();

        return dummyBoard;
    }

    @Override
    public boolean customizedInsertDummiesIntoDatabase() {
        Blog randomlySelectedBlog = this.blogs.get(random.nextInt(blogs.size()));
        List<Board> batchDummyBoards = new ArrayList<>(BATCH_SIZE);

        int page = 820;
        long totalInserted = 0;
        while (true) {
            List<CrawledBoard> crawledBoards = crawledBoardRepository.findAll(PageRequest.of(page, BATCH_SIZE, Sort.unsorted())).getContent();

            if (crawledBoards.size() < BATCH_SIZE) {
                break;
            }

            for (CrawledBoard crawledBoard : crawledBoards) {
                Board dummyBoard = Board.builder()
                        .title(crawledBoard.getTitle())
                        .content(crawledBoard.getContent())
                        .blog(randomlySelectedBlog)
                        .viewCount(0L)
                        .wishes(null)
                        .build();
                batchDummyBoards.add(dummyBoard);
            }

            System.out.println("Inserting 1,000 boards into test-app db");
            repository.saveAll(batchDummyBoards);
            System.out.println("Succeeded inserting 1,000 boards into test-app db!");

            totalInserted += BATCH_SIZE;
            page += 1;
            batchDummyBoards.clear();
            System.out.println("total " + totalInserted + " inserted. page : " + page);
        }

        return true;
    }
}
