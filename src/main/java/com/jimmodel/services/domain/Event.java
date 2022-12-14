package com.jimmodel.services.domain;


import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "task")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Event extends BaseEntity {

    @Builder
    public Event(UUID id, String title, Collection<Slot> slots, Collection<Model> relatedModels, Client client, String personInCharge, String mediaReleased, String periodReleased, String territoriesReleased, String workingHour, String overtimePerHour, String feeAsAgreed, String termOfPayment, String cancellationFee, String contractDetails, String note, TYPE type) {
        this.id = id;
        this.title = title;
        this.slots = slots;
        if(this.slots != null)this.slots.forEach(slot -> slot.setEvent(this));
        this.relatedModels = relatedModels;
        this.client = client;
        this.personInCharge = personInCharge;
        this.mediaReleased = mediaReleased;
        this.periodReleased = periodReleased;
        this.territoriesReleased = territoriesReleased;
        this.workingHour = workingHour;
        this.overtimePerHour = overtimePerHour;
        this.feeAsAgreed = feeAsAgreed;
        this.termOfPayment = termOfPayment;
        this.cancellationFee = cancellationFee;
        this.contractDetails = contractDetails;
        this.note = note;
        this.type = type;
    }

    public enum TYPE {
        OPTION,
        JOB,
        REMINDER
    }
    public interface JobInfo {}
    public interface OptionInfo {}
    public interface ReminderInfo {}

    @Id
    @GeneratedValue(generator = "uuid-generator")
    @GenericGenerator(name = "uuid-generator",strategy = "com.jimmodel.services.config.UUIDGenerator")
    @EqualsAndHashCode.Include private UUID id;
    @NotNull(groups = {JobInfo.class, OptionInfo.class, ReminderInfo.class}, message = "Title cannot be blank.")
    protected String title;
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    protected Collection<Slot> slots;
    @ManyToMany
    @Valid
    protected Collection<Model> relatedModels;
    @NotNull(message = "Client cannot be blank.", groups = {JobInfo.class})
    @OneToOne(cascade = CascadeType.ALL)
    @Valid
    private Client client;
    @NotBlank(message = "Person in charge cannot be blank.", groups = {JobInfo.class})
    private String personInCharge;
    @NotBlank(message = "Media released cannot be blank.", groups = {JobInfo.class, OptionInfo.class})
    private String mediaReleased;
    @NotBlank(message = "Person released cannot be blank.", groups = {JobInfo.class})
    private String periodReleased;
    @NotBlank(message = "Territories released cannot be blank.", groups = {JobInfo.class, OptionInfo.class})
    private String territoriesReleased;
    @NotBlank(message = "Working hour cannot be blank.", groups = {JobInfo.class, OptionInfo.class})
    private String workingHour;
    @NotBlank(message = "Overtime per hour cannot be blank.", groups = {JobInfo.class})
    private String overtimePerHour;
    @NotBlank(message = "Fee as agreed cannot be blank.", groups = {JobInfo.class})
    private String feeAsAgreed;
    @NotBlank(message = "Term of payment cannot be blank.", groups = {JobInfo.class})
    private String termOfPayment;
    @NotBlank(message = "Cancellation fee cannot be blank.", groups = {JobInfo.class})
    private String cancellationFee;
    private String contractDetails;
    private String note;
    @NotNull(message = "Type cannot be blank.", groups = {JobInfo.class, OptionInfo.class, ReminderInfo.class})
    private TYPE type;
    public Event(String title, Collection<Slot> slots, Collection<Model> relatedModels){
        this.title = title;
        this.slots = slots;
        this.relatedModels = relatedModels;
    }

    public Event(UUID id, String title, Collection<Slot> slots, Collection<Model> relatedModels){
        this.title = title;
        this.slots = slots;
        this.relatedModels = relatedModels;
    }

    public Event(UUID id, String title, Collection<Slot> slots){
        this.id = id;
        this.title = title;
        this.slots = slots;
    }

    public static Event jobBuilder(UUID id, String title, Collection<Slot> slots, Collection<Model> relatedModels, Client client, String personInCharge, String mediaReleased, String periodReleased, String territoriesReleased, String workingHour, String overtimePerHour, String feeAsAgreed, String termOfPayment, String cancellationFee, String contractDetails, String note){
        return Event.builder()
                .id(id)
                .title(title)
                .slots(slots)
                .client(client)
                .personInCharge(personInCharge)
                .mediaReleased(mediaReleased)
                .periodReleased(periodReleased)
                .territoriesReleased(territoriesReleased)
                .workingHour(workingHour)
                .overtimePerHour(overtimePerHour)
                .feeAsAgreed(feeAsAgreed)
                .termOfPayment(termOfPayment)
                .cancellationFee(cancellationFee)
                .contractDetails(contractDetails)
                .relatedModels(relatedModels)
                .note(note)
                .type(TYPE.JOB)
                .build();
    }

    public static Event optionBuilder(UUID id, String title, Collection<Slot> slots, Collection<Model> relatedModels, String mediaReleased, String territoriesReleased, String workingHour, String note){
        return Event.builder()
                .id(id)
                .title(title)
                .slots(slots)
                .relatedModels(relatedModels)
                .mediaReleased(mediaReleased)
                .territoriesReleased(territoriesReleased)
                .workingHour(workingHour)
                .note(note)
                .type(TYPE.OPTION)
                .build();
    }

    public static Event reminderBuilder(UUID id, String title, Collection<Slot> slots, Collection<Model> relatedModels, String not){
        return Event.builder()
                .id(id)
                .title(title)
                .slots(slots)
                .relatedModels(relatedModels)
                .note(not)
                .type(TYPE.REMINDER)
                .build();
    }


    public void setRelatedModels(Collection<Model> relatedModels){
        if (this.relatedModels == null){
            this.relatedModels = new HashSet<>();
        }else {
            this.relatedModels.clear();
        }
        this.relatedModels.clear();
        if(relatedModels != null){
            this.relatedModels.addAll(relatedModels);
        }
    }

    public void setSlots(Collection<Slot> slots){
        if(this.slots == null){
            this.slots = new HashSet<>();
        }else {
            this.slots.clear();
        }
        if(slots != null){
            this.slots.addAll(slots);
            this.slots.forEach(event -> event.setEvent(this));
        }
    }

    public void addSlot(Slot slot){
        slot.setEvent(this);
        this.slots.add(slot);
    }

    public void removeSlot(Slot slot){
        this.slots.remove(slot);
        slot.setEvent(null);
    }

}
