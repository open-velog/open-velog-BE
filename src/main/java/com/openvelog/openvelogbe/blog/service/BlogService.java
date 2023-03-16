package com.openvelog.openvelogbe.blog.service;

import com.openvelog.openvelogbe.blog.dto.BlogRequestDto;
import com.openvelog.openvelogbe.blog.dto.BlogResponseDto;
import com.openvelog.openvelogbe.common.dto.ErrorMessage;
import com.openvelog.openvelogbe.common.entity.Blog;
import com.openvelog.openvelogbe.common.entity.Member;
import com.openvelog.openvelogbe.common.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
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

        return BlogResponseDto.of(blogRepository.save(blog));
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

        return BlogResponseDto.of(blog);
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
    public BlogResponseDto getBlog(String blogId) {
        Blog blog = blogRepository.findByUserIdJPQL(blogId).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.NO_BLOG.getMessage())
        );

        return BlogResponseDto.ofNoBoards(blog);
    }
}
