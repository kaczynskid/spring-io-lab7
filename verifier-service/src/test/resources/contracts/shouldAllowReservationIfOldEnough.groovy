package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method('POST')
        url('/check')
        body([
            name: 'John',
            age: 22
        ])
        headers {
            header('Content-Type', 'application/json')
        }
    }
    response {
        status 200
        body([
            eligible: true
        ])
        headers {
            header('Content-Type', value(
                consumer('application/json'),
                producer(regex('application/json.*'))
            ))
        }
    }
}
