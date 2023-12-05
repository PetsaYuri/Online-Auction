package com.OnlineAuction.Services;

import com.OnlineAuction.DTO.ResultOfActionDTO;
import com.OnlineAuction.Models.Auction;
import com.OnlineAuction.Models.ResultOfAuction;
import com.OnlineAuction.Repositories.ResultsOfAuctionsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class ResultOfAuctionService {

    private final ResultsOfAuctionsRepository resultsOfAuctionsRepository;

    private final AuctionResultService auctionResultService;

    @Autowired
    public ResultOfAuctionService(ResultsOfAuctionsRepository resultsOfAuctionsRepository, AuctionResultService auctionResultService) {
        this.resultsOfAuctionsRepository = resultsOfAuctionsRepository;
        this.auctionResultService = auctionResultService;
    }

    public ResultOfAuction getResultById(Long id) {
        if (!resultsOfAuctionsRepository.existsById(id)) {
            throw new EntityNotFoundException();
        }
        return resultsOfAuctionsRepository.getReferenceById(id);
    }

    public List<ResultOfAuction> getAll(Pageable pageable) {
        return resultsOfAuctionsRepository.findAll(pageable).toList();
    }

    public List<ResultOfAuction> getByNameAuction(String name, Pageable pageable) {
        return resultsOfAuctionsRepository.findByNameAuctionContainsIgnoreCase(name, pageable).toList();
    }

    public List<ResultOfAuction> getByStartAndEnds(Timestamp start, Timestamp end, Pageable pageable) {
        return resultsOfAuctionsRepository.findByStartAfterAndEndsBefore(start, end, pageable).toList();
    }

    public List<ResultOfAuction> getAfterStart(Timestamp start, Pageable pageable) {
        return resultsOfAuctionsRepository.findByStartAfter(start, pageable).toList();
    }

    public List<ResultOfAuction> getBeforeEnd(Timestamp end, Pageable pageable) {
        return resultsOfAuctionsRepository.findByEndsBefore(end, pageable).toList();
    }

    public List<ResultOfAuction> getByNameAuctionAndStartAndEnds(String name, Timestamp start, Timestamp end, Pageable pageable) {
        return resultsOfAuctionsRepository.findByNameAuctionContainsIgnoreCaseAndStartAfterAndEndsBefore(name, start, end, pageable).toList();
    }

    public List<ResultOfAuction> getByNameAuctionAndStart(String name, Timestamp start, Pageable pageable) {
        return resultsOfAuctionsRepository.findByNameAuctionContainsIgnoreCaseAndStartAfter(name, start, pageable).toList();
    }

    public List<ResultOfAuction> getByNameAuctionAndEnds(String name, Timestamp end, Pageable pageable) {
        return resultsOfAuctionsRepository.findByNameAuctionContainsIgnoreCaseAndEndsBefore(name, end, pageable).toList();
    }

    @Transactional
    public ResultOfAuction create(Long idAuction) {
        Auction auction = auctionResultService.getOneById(idAuction);
        ResultOfAuction newResultOfAuction = new ResultOfAuction(new ResultOfActionDTO(auction));
        resultsOfAuctionsRepository.save(newResultOfAuction);
        auctionResultService.setResultOfAuction(newResultOfAuction);
        return newResultOfAuction;
    }

    /*public ResultOfAuction update(Long id, ResultOfActionDTO resultOfActionDTO) {
        ResultOfAuction existResult = resultsOfAuctionsRepository.getReferenceById(id);

        if (resultOfActionDTO.auction().getId() != null &&)
    }*/

    public boolean delete(Long id) {
        ResultOfAuction resultOfAuction = resultsOfAuctionsRepository.getReferenceById(id);
        auctionResultService.removeResultOfAuction(resultOfAuction.getAuction());
        resultsOfAuctionsRepository.delete(resultOfAuction);
        return true;
    }

    public void removeResultFromAuction(ResultOfAuction resultOfAuction) {
        resultOfAuction.setAuction(null);
        resultsOfAuctionsRepository.save(resultOfAuction);
    }
}