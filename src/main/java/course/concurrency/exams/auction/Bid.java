package course.concurrency.exams.auction;

public class Bid {
    private final Long id; // ID заявки
    private final Long participantId; // ID участника
    private final Long price; // предложенная цена

    public Bid(Long id, Long participantId, Long price) {
        this.id = id;
        this.participantId = participantId;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public Long getPrice() {
        return price;
    }
}
