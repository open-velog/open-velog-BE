package com.openvelog.openvelogbe.blog.service;

import com.openvelog.openvelogbe.blog.dto.BlogRequestDto;
import com.openvelog.openvelogbe.blog.dto.BlogResponseDto;
import com.openvelog.openvelogbe.common.dto.ErrorMessage;
import com.openvelog.openvelogbe.common.entity.Blog;
import com.openvelog.openvelogbe.common.entity.Member;
import com.openvelog.openvelogbe.common.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;

    @Transactional
    public BlogResponseDto createBlog(BlogRequestDto.BlogAdd dto, Member member) {

        if(blogRepository.findByMemberId(member.getId()).isPresent()) {
            throw new IllegalArgumentException(ErrorMessage.BLOG_DUPLICATION.getMessage());
        }

        Blog blog = Blog.create(dto, member);

        return BlogResponseDto.ofNoBoards(blogRepository.save(blog));
    }

    @Transactional
    public BlogResponseDto updateBlog(Long blogId, BlogRequestDto.BlogUpdate dto, Member member) {
        Blog blog = blogRepository.findById(blogId).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.NO_BLOG.getMessage())
        );

        if (!blog.getMember().getId().equals(member.getId())) {
            throw new AccessDeniedException(ErrorMessage.ACCESS_DENIED.getMessage());
        }

        blog.update(dto);

        return BlogResponseDto.ofNoBoards(blog);
    }

    @Transactional
    public void deleteBlog(Long blogId, Member member) {

        Blog blog = blogRepository.findById(blogId).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.NO_BLOG.getMessage())
        );

        if (!blog.getMember().getId().equals(member.getId())) {
            throw new AccessDeniedException(ErrorMessage.ACCESS_DENIED.getMessage());
        }

        blogRepository.delete(blog);
    }

    @Transactional(readOnly = true)
    public BlogResponseDto getBlog(String userId) {
        Object[] objects = blogRepository.findByUserIdJPQL(userId).stream().findFirst().orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.NO_BLOG.getMessage())
        );

        return BlogResponseDto.of((Blog)objects[0], (Long)objects[1], (Long)objects[2]);
    }

    @Transactional(readOnly = true)
    public Page<BlogResponseDto> getBlogsByViewCount(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "viewCountSum", "bb.createdAt"));
        Page<Object[]> blogPage = blogRepository.findAllOrderByBoardsCountedDesc(pageRequest);
        return blogPage.map(objects -> BlogResponseDto.of((Blog)objects[0], (Long)objects[1], (Long)objects[2]));
    }


    @Transactional(readOnly = true)
    public Page<BlogResponseDto> getBlogsByWishes(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "wishCountSum", "bb.createdAt"));
        Page<Object[]> blogPage = blogRepository.findAllOrderByBoardsCountedDesc(pageRequest);
        return blogPage.map(objects -> BlogResponseDto.of((Blog)objects[0], (Long)objects[1], (Long)objects[2]));
    }

}
