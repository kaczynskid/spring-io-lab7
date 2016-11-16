package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method('POST')
        url('/check')
        body([
            name: 'Jake',
            age: 17
        ])
        headers {
            header('Content-Type', 'application/json')
        }
    }
    response {
        status 200
        body([
            eligible: false
        ])
        headers {
            header('Content-Type', 'application/json')
        }
    }
}
