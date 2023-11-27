package com.OnlineAuction.Services;

import com.OnlineAuction.DTO.ResultOfActionDTO;
import com.OnlineAuction.Models.Auction;
import com.OnlineAuction.Models.ResultOfAuction;
import com.OnlineAuction.Repositories.ResultsOfAuctionsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<ResultOfAuction> getAll() {
        return resultsOfAuctionsRepository.findAll();
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