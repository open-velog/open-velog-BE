package com.openvelog.openvelogbe.dummy;

import com.openvelog.openvelogbe.common.entity.Blog;
import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.repository.BlogRepository;
import com.openvelog.openvelogbe.common.repository.BoardRepository;
import com.openvelog.openvelogbe.crawling.entity.CrawledBoard;
import com.openvelog.openvelogbe.crawling.repository.CrawledBoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Lazy
public class BoardDummyGenerator extends DummyGenerator<Board, BoardRepository> {

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
        this.crawledBoards = crawledBoardRepository.findAllWithLimit(10000);
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
}
