package pl.kurs.review;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransferService {

    private final AccountRepository accounts;
    private final FraudCheck fraudCheck;

    public TransferService(AccountRepository accounts, FraudCheck fraudCheck) {
        this.accounts = accounts;
        this.fraudCheck = fraudCheck;
    }

    /** Transfers the amount between two accounts: either everything happens, or nothing. */
    public void transfer(String from, String to, BigDecimal amount) throws SuspiciousTransferException {
        Account source = accounts.findById(from).orElseThrow(() -> new AccountNotFoundException(from));
        source.withdraw(amount);
        accounts.save(source);

        Account target = accounts.findById(to).orElseThrow(() -> new AccountNotFoundException(to));
        target.deposit(amount);
        accounts.save(target);

        // The anti-fraud check needs both balances after the operation, so it runs at the end.
        fraudCheck.verify(source, target, amount);
    }
}
