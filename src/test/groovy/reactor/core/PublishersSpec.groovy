/*
 * Copyright (c) 2011-2016 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package reactor.core

import reactor.core.test.TestSubscriber
import spock.lang.Specification

import static reactor.core.publisher.Flux.fromIterable

/**
 * @author Stephane Maldini
 */
class PublishersSpec extends Specification {

  def "Error handling with onErrorReturn"() {

	given: "Iterable publisher of 1000 to read queue"
	def pub = fromIterable(1..1000).map{ d ->
	  if (d == 3) {
		throw new Exception('test')
	  }
	  d
	}

	when: "read the queue"
	def s = TestSubscriber.create()
	pub.onErrorReturn(100000).subscribe(s)

	then: "queues values correct"
	s.awaitAndAssertNextValues(1, 2, 100000)
			.assertComplete()
  }

  def "Error handling with onErrorResume"() {

	given: "Iterable publisher of 1000 to read queue"
	def pub = fromIterable(1..1000).map{ d ->
	  if (d == 3) {
		throw new Exception('test')
	  }
	  d
	}

	when: "read the queue"
	def s = TestSubscriber.create()
	pub.switchOnError(fromIterable(9999..10002)).subscribe(s)

	then: "queues values correct"
	s.awaitAndAssertNextValues(1, 2, 9999, 10000, 10001, 10002)
			.assertComplete()
  }

}
