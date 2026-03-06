package com.balanced.tag.service;

import com.balanced.common.exception.ConflictException;
import com.balanced.common.exception.ResourceNotFoundException;
import com.balanced.tag.dto.CreateTagInput;
import com.balanced.tag.dto.UpdateTagInput;
import com.balanced.tag.entity.Tag;
import com.balanced.common.enums.Status;
import com.balanced.tag.mapper.TagMapper;
import com.balanced.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Transactional(readOnly = true)
    public List<Tag> listAllByWorkspaceId(UUID workspaceId) {
        return tagRepository.findAllByWorkspaceId(workspaceId);
    }

    @Transactional(readOnly = true)
    public Tag getTag(UUID tagId, UUID workspaceId) {
        return tagRepository.findByIdAndWorkspaceId(tagId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
    }

    @Transactional
    public Tag createTag(UUID workspaceId, CreateTagInput dto) {
        if (tagRepository.existsByWorkspaceIdAndNameIgnoreCase(workspaceId, dto.getName())) {
            throw new ConflictException("A tag named '" + dto.getName() + "' already exists");
        }

        Tag tag = Tag.builder()
                .workspaceId(workspaceId)
                .name(dto.getName())
                .description(dto.getDescription())
                .status(Status.ACTIVE)
                .build();

        log.info("Created tag '{}'", dto.getName());
        return tagRepository.save(tag);
    }

    @Transactional
    public Tag updateTag(UUID tagId, UUID workspaceId, UpdateTagInput dto) {
        Tag tag = getTag(tagId, workspaceId);

        if (dto.getName() != null
                && !dto.getName().equalsIgnoreCase(tag.getName())
                && tagRepository.existsByWorkspaceIdAndNameIgnoreCase(workspaceId, dto.getName())) {
            throw new ConflictException("A tag named '" + dto.getName() + "' already exists");
        }

        tagMapper.updateEntity(dto, tag);
        log.info("Updating tag '{}' ({})", tag.getName(), tagId);
        return tagRepository.save(tag);
    }

    @Transactional
    public void deleteTag(UUID tagId, UUID workspaceId) {
        Tag tag = getTag(tagId, workspaceId);
        log.info("Deleting tag '{}' ({})", tag.getName(), tagId);
        tagRepository.delete(tag);
    }
}
