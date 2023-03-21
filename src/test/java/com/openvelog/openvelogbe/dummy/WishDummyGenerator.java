package com.openvelog.openvelogbe.dummy;

import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.entity.BoardWishMember;
import com.openvelog.openvelogbe.common.entity.Member;
import com.openvelog.openvelogbe.common.repository.BoardRepository;
import com.openvelog.openvelogbe.common.repository.BoardWishMemberRepository;
import com.openvelog.openvelogbe.common.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Lazy
public class WishDummyGenerator  extends DummyGenerator<BoardWishMember, BoardWishMemberRepository> {

    private final int BOARD_SIZE;

    private final List<Member> members;

    private BoardRepository boardRepository;

    private MemberRepository memberRepository;

    @Autowired
    WishDummyGenerator(
            BoardWishMemberRepository repository,
            BoardRepository boardRepository,
            MemberRepository memberRepository
    ) {
        super(repository);
        this.boardRepository = boardRepository;
        this.memberRepository = memberRepository;
        this.BOARD_SIZE = 840000;
        this.members = memberRepository.findAll();
    }

    @Override
    public BoardWishMember generateDummyEntityOfThis() {
        BoardWishMember boardWishMember = BoardWishMember.builder()
                .board(Board.builder().id((long)random.nextInt(BOARD_SIZE - 4) + 4).build())
                .member(members.get(random.nextInt(members.size())))
                .build();
        return boardWishMember;
    }

    @Override
    public boolean insertDummiesIntoDatabase(int dummyCount) throws InterruptedException {
        int totalInsertion = 0;
        int batchSize = 100;

        while (totalInsertion < dummyCount) {
            int targetDummyCount = Math.min(batchSize, dummyCount);

            List<BoardWishMember> batchDummyWishes = new ArrayList<>(targetDummyCount);
            for (int i = 0; i < targetDummyCount; ++i) {
                BoardWishMember boardWishMember = this.generateDummyEntityOfThis();
                batchDummyWishes.add(boardWishMember);
            }

            try {
                this.repository.saveAll(batchDummyWishes);
            } catch (Exception e) {
                System.out.println("Error occurs while inserting wish dummy data");
                Thread.sleep(300);
                continue;
            }

            totalInsertion += targetDummyCount;
            System.out.print("total Insertion: " + totalInsertion + " ");
            System.out.println("dummy count " + (dummyCount - totalInsertion) + " left to be inserted.");
        }

        return true;
    }


}
