package com.jimmodel.services.service;

import com.jimmodel.services.exception.ValidationException;
import com.jimmodel.services.domain.*;
import com.jimmodel.services.exception.ResourceNotFoundException;
import com.jimmodel.services.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobServiceImp implements JobService{
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    Validator validator;

    @Override
    public Event save(Event job) {

        Set<ConstraintViolation<BaseEntity>>  violations = validator.validate(job, Event.JobInfo.class);
        if (!violations.isEmpty()){
            throw new ValidationException(String.join(",",  violations.stream().map(violation -> violation.getMessage()).collect(Collectors.toList())));
        }

        Collection<Model> relatedModels = new HashSet<>();
        Client client = null;


        if(job.getClient() != null){
            Optional<Client> existingClient = clientRepository.findById(job.getClient().getId());
            if(existingClient.isPresent()) client = existingClient.get();
            else throw new ResourceNotFoundException(String.format("Client with id %s does not exist", job.getClient().getId()));
        }


        if (job.getRelatedModels() != null) {
            job.getRelatedModels().forEach(model -> {
                Optional<Model> existingModel = modelRepository.findById(model.getId());
                if (existingModel.isPresent()) relatedModels.add(existingModel.get());
                else throw new ResourceNotFoundException(String.format("Model with id %s does not exist.", model.getId()));
            });
        }

        job.setRelatedModels(relatedModels);
        job.setClient(client);



        Event savedJob = eventRepository.save(job);
        eventPublisher.publishEvent(new EventSavedEvent(savedJob));
        return savedJob;
    }

    @Override
    @Transactional
    public Event saveById(UUID id, Event updatedJob) {
        Event job = this.findById(id);
//        Set<ConstraintViolation<BaseEntity>> violations = validator.validate(updatedJob);
//        if (!violations.isEmpty()){
//            throw new ValidationException("Job validation failed", violations);
//        }
        job.setSlots(updatedJob.getSlots());
//        if(job.getEvents() != null) job.getEvents().forEach(event -> event.setTask(job));
        job.setTitle(updatedJob.getTitle());
        job.setRelatedModels(updatedJob.getRelatedModels());
        job.setClient(updatedJob.getClient());
        job.setPersonInCharge(updatedJob.getPersonInCharge());
        job.setMediaReleased(updatedJob.getMediaReleased());
        job.setPeriodReleased(updatedJob.getPeriodReleased());
        job.setTerritoriesReleased(updatedJob.getTerritoriesReleased());
        job.setWorkingHour(updatedJob.getWorkingHour());
        job.setOvertimePerHour(updatedJob.getOvertimePerHour());
        job.setFeeAsAgreed(updatedJob.getFeeAsAgreed());
        job.setTermOfPayment(updatedJob.getTermOfPayment());
        job.setCancellationFee(updatedJob.getCancellationFee());
        job.setContractDetails(updatedJob.getContractDetails());
        return eventRepository.save(job);
    }

    @Override
    public Event findById(UUID id) {
        return eventRepository.findByTypeAndId(Event.TYPE.JOB, id).orElseThrow(() -> new ResourceNotFoundException(String.format("Job with id %s does not exist.", id)));
    }

    @Override
    public Page<Event> findAll(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        Sort sort = sortBy.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Event> jobPage =  eventRepository.findAllByType(Event.TYPE.JOB, pageable);
        return jobPage;
    }

    @Override
    public void deleteById(UUID id) {
        if(!eventRepository.existsByTypeAndId(Event.TYPE.JOB, id)){
            throw new ResourceNotFoundException(String.format("Job with id %s does not exist.", id));
        }
        eventRepository.deleteById(id);
    }

    @Override
    public Page<Event> search(String searchTerm, Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        Sort sort = sortBy.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return eventRepository.search(Event.TYPE.JOB, searchTerm, pageable);
    }
}
