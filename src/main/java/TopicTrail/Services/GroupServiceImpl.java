package TopicTrail.Services;

import TopicTrail.Domain.Group;
import TopicTrail.Domain.Post;
import TopicTrail.Repositories.GroupRepository;
import TopicTrail.Repositories.PostRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;

    public GroupServiceImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public Mono<Group> save(Group group) {
        return groupRepository.save(group);
    }

    @Override
    public Mono<Void> delete(String Id) {
        return groupRepository.deleteById(Id);
    }

    @Override
    public Mono<Group> findByTitle(String title) {
        return groupRepository.findByTitle(title);
    }

    @Override
    public Flux<Group> findByTitleContainsIgnoreCase(String title) {
        return groupRepository.findByTitleContainsIgnoreCase(title);
    }

    @Override
    public Flux<Group> getGroups(){
        return groupRepository.findAll();
    }

    @Override
    public Mono<Group> update(Group group) {
        return groupRepository.findById(group.getId())
                .map(u -> group)
                .flatMap(groupRepository::save);
    }
}
